import { useEffect, useState } from "react"
import { Navigate, Route, Routes } from "react-router-dom"
import { ApiError } from "@/api/http-client"
import { AuthTransitionScreen } from "@/components/auth-transition-screen"
import { GithubLoginPage } from "@/components/github-login-page"
import { LogoutPage } from "@/components/logout-page"
import { AppShell } from "@/components/app-shell"
import { caseManagerApi } from "@/api/case-manager-client"
import { env } from "@/config/env"
import { AuthProvider } from "@/lib/auth-context"
import { AssignmentGroupsPage } from "@/pages/assignment-groups-page"
import { AssignmentGroupCreatePage } from "@/pages/assignment-group-create-page"
import { AssignmentGroupDetailsPage } from "@/pages/assignment-group-details-page"
import { AssignedToMePage } from "@/pages/assigned-to-me-page"
import { AssignedToMyGroupsPage } from "@/pages/assigned-to-my-groups-page"
import { AuditPage } from "@/pages/audit-page"
import { DashboardPage } from "@/pages/dashboard-page"
import { NotFoundPage } from "@/pages/not-found-page"
import { NotificationsPage } from "@/pages/notifications-page"
import { SettingsPage } from "@/pages/settings-page"
import { TicketDetailsPage } from "@/pages/ticket-details-page"
import { TicketCreatePage } from "@/pages/ticket-create-page"
import { MyReportedTicketsPage } from "@/pages/my-reported-tickets-page"
import { ReportIssuePage } from "@/pages/report-issue-page"
import { UserCreatePage } from "@/pages/user-create-page"
import { UserDetailsPage } from "@/pages/user-details-page"
import { TicketsPage } from "@/pages/tickets-page"
import { UsersPage } from "@/pages/users-page"
import type { RoleName, UserResponse } from "@/types/api"

const AUTH_REDIRECT_FLAG = "case-manager-auth-redirect-started"

function App() {
  const [authPhase, setAuthPhase] = useState<"idle" | "signing-in" | "welcome">("idle")
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [authError, setAuthError] = useState<string | null>(null)
  const [user, setUser] = useState<UserResponse | null>(null)
  const [previewRole, setPreviewRole] = useState<RoleName | null>(null)
  const [hasCheckedSession, setHasCheckedSession] = useState(false)

  const wait = (durationMs: number) =>
    new Promise<void>((resolve) => {
      window.setTimeout(resolve, durationMs)
    })

  const checkSession = async ({ showWelcome }: { showWelcome: boolean }) => {
    setAuthPhase("signing-in")
    try {
      const currentUser = await caseManagerApi.auth.me()
      setUser(currentUser)
      setIsAuthenticated(true)
      setAuthError(null)
      if (showWelcome) {
        setAuthPhase("welcome")
        await wait(1800)
      }
      setAuthPhase("idle")
    } catch (requestError) {
      if (requestError instanceof ApiError && (requestError.status === 401 || requestError.status === 403)) {
        setIsAuthenticated(false)
        setUser(null)
        setAuthError(null)
      } else {
        // For OAuth redirects initiated by fetch (302 -> github), browsers report network/CORS errors.
        // Keep UX clean by treating these as not-yet-authenticated unless user is already in-session.
        const message = requestError instanceof Error ? requestError.message : "Unexpected error"
        setIsAuthenticated(false)
        setUser(null)
        setAuthError(message.includes("Failed to fetch") ? null : message)
      }
      setAuthPhase("idle")
    } finally {
      setHasCheckedSession(true)
    }
  }

  useEffect(() => {
    const startedFromRedirect = localStorage.getItem(AUTH_REDIRECT_FLAG) === "1"
    if (startedFromRedirect) {
      localStorage.removeItem(AUTH_REDIRECT_FLAG)
    }
    void checkSession({ showWelcome: startedFromRedirect })
  }, [])

  if (!hasCheckedSession || authPhase === "signing-in") {
    return <AuthTransitionScreen phase="signing-in" />
  }

  if (authPhase === "welcome") {
    return <AuthTransitionScreen phase="welcome" userName={user?.userName} />
  }

  if (!isAuthenticated || !user) {
    return (
      <GithubLoginPage
        loginUrl={env.githubLoginUrl}
        error={authError}
      />
    )
  }

  const canUseRolePreview = user.roles.includes("ADMIN")
  const effectiveUser = previewRole ? { ...user, roles: [previewRole] } : user
  const isAdmin = effectiveUser.roles.includes("ADMIN")
  const isAgent = effectiveUser.roles.includes("AGENT")
  const isViewer = effectiveUser.roles.includes("VIEWER")
  const isStaff = isAdmin || isAgent
  const defaultAppPath = isStaff ? "/app/dashboard" : "/app/my-reported-tickets"

  return (
    <AuthProvider value={{ user: effectiveUser }}>
      <Routes>
        <Route path="/" element={<Navigate to={defaultAppPath} replace />} />
        <Route
          path="/login"
          element={<GithubLoginPage loginUrl={env.githubLoginUrl} error={authError} />}
        />
        <Route
          path="/logout"
          element={
            <LogoutPage
              onCancel={() => window.history.back()}
              onLoggedOut={() => {
                setIsAuthenticated(false)
                setUser(null)
                setAuthError(null)
                setAuthPhase("idle")
              }}
              onError={(message) => setAuthError(message)}
            />
          }
        />
        <Route
          path="/app"
          element={
            <AppShell
              user={effectiveUser}
              canUseRolePreview={canUseRolePreview}
              previewRole={previewRole}
              onSetPreviewRole={setPreviewRole}
            />
          }
        >
          {isStaff && <Route path="dashboard" element={<DashboardPage />} />}
          {isStaff && <Route path="tickets" element={<TicketsPage />} />}
          {isStaff && <Route path="tickets/assigned-to-me" element={<AssignedToMePage />} />}
          {isStaff && <Route path="tickets/assigned-to-my-groups" element={<AssignedToMyGroupsPage />} />}
          <Route path="my-reported-tickets" element={<MyReportedTicketsPage />} />
          {isStaff && <Route path="tickets/new" element={<TicketCreatePage />} />}
          {!isStaff && !isViewer && <Route path="report-issue" element={<ReportIssuePage />} />}
          <Route path="tickets/:ticketId" element={<TicketDetailsPage />} />
          {isAdmin && <Route path="assignment-groups" element={<AssignmentGroupsPage />} />}
          {isAdmin && <Route path="assignment-groups/new" element={<AssignmentGroupCreatePage />} />}
          {isAdmin && <Route path="assignment-groups/:groupId" element={<AssignmentGroupDetailsPage />} />}
          {isAdmin && <Route path="users" element={<UsersPage />} />}
          {isAdmin && <Route path="users/new" element={<UserCreatePage />} />}
          {isAdmin && <Route path="users/:userId" element={<UserDetailsPage />} />}
          {isAdmin && <Route path="audit" element={<AuditPage />} />}
          <Route path="notifications" element={<NotificationsPage />} />
          <Route path="settings" element={<SettingsPage />} />
          <Route path="*" element={<Navigate to={defaultAppPath} replace />} />
        </Route>
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </AuthProvider>
  )
}

export default App
