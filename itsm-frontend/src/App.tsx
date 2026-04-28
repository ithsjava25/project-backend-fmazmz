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
import { UserCreatePage } from "@/pages/user-create-page"
import { UserDetailsPage } from "@/pages/user-details-page"
import { TicketsPage } from "@/pages/tickets-page"
import { UsersPage } from "@/pages/users-page"
import type { UserResponse } from "@/types/api"

const AUTH_REDIRECT_FLAG = "case-manager-auth-redirect-started"

function App() {
  const [authPhase, setAuthPhase] = useState<"idle" | "signing-in" | "welcome">("idle")
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [authError, setAuthError] = useState<string | null>(null)
  const [user, setUser] = useState<UserResponse | null>(null)
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

  return (
    <AuthProvider value={{ user }}>
      <Routes>
        <Route path="/" element={<Navigate to="/app/dashboard" replace />} />
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
        <Route path="/app" element={<AppShell user={user} />}>
          <Route path="dashboard" element={<DashboardPage />} />
          <Route path="tickets" element={<TicketsPage />} />
          <Route path="tickets/assigned-to-me" element={<AssignedToMePage />} />
          <Route path="tickets/assigned-to-my-groups" element={<AssignedToMyGroupsPage />} />
          <Route path="tickets/new" element={<TicketCreatePage />} />
          <Route path="tickets/:ticketId" element={<TicketDetailsPage />} />
          <Route path="assignment-groups" element={<AssignmentGroupsPage />} />
          <Route path="assignment-groups/new" element={<AssignmentGroupCreatePage />} />
          <Route path="assignment-groups/:groupId" element={<AssignmentGroupDetailsPage />} />
          <Route path="users" element={<UsersPage />} />
          <Route path="users/new" element={<UserCreatePage />} />
          <Route path="users/:userId" element={<UserDetailsPage />} />
          <Route path="audit" element={<AuditPage />} />
          <Route path="notifications" element={<NotificationsPage />} />
          <Route path="settings" element={<SettingsPage />} />
        </Route>
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </AuthProvider>
  )
}

export default App
