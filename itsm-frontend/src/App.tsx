import { useEffect, useState } from "react"
import { ApiError } from "@/api/http-client"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Separator } from "@/components/ui/separator"
import { AuthTransitionScreen } from "@/components/auth-transition-screen"
import { OpenApiPanel } from "@/components/openapi-panel"
import { CreateAssignmentGroupForm } from "@/components/create-assignment-group-form"
import { CreateTicketForm } from "@/components/create-ticket-form"
import { GithubLoginPage } from "@/components/github-login-page"
import { LogoutPage } from "@/components/logout-page"
import { caseManagerApi } from "@/api/case-manager-client"
import { env } from "@/config/env"
import type { TicketResponse, UserResponse } from "@/types/api"

const AUTH_REDIRECT_FLAG = "case-manager-auth-redirect-started"

function App() {
  const [authPhase, setAuthPhase] = useState<"idle" | "signing-in" | "welcome">("idle")
  const [activePage, setActivePage] = useState<"home" | "logout">("home")
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [loading, setLoading] = useState(true)
  const [authError, setAuthError] = useState<string | null>(null)
  const [dataError, setDataError] = useState<string | null>(null)
  const [user, setUser] = useState<UserResponse | null>(null)
  const [tickets, setTickets] = useState<TicketResponse[]>([])

  const wait = (durationMs: number) =>
    new Promise<void>((resolve) => {
      window.setTimeout(resolve, durationMs)
    })

  const loadDashboard = async () => {
    setLoading(true)
    try {
      const page = await caseManagerApi.tickets.list()
      setTickets(page.items)
      setDataError(null)
    } catch (requestError) {
      const message = requestError instanceof Error ? requestError.message : "Unexpected error"
      setDataError(message)
    } finally {
      setLoading(false)
    }
  }

  const checkSession = async () => {
    setAuthPhase("signing-in")
    try {
      const currentUser = await caseManagerApi.auth.me()
      setUser(currentUser)
      setIsAuthenticated(true)
      setAuthError(null)
      setAuthPhase("welcome")
      await Promise.all([loadDashboard(), wait(1800)])
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
    }
  }

  useEffect(() => {
    if (localStorage.getItem(AUTH_REDIRECT_FLAG) === "1") {
      localStorage.removeItem(AUTH_REDIRECT_FLAG)
      void checkSession()
    } else {
      setLoading(false)
    }
  }, [])

  if (authPhase === "signing-in") {
    return <AuthTransitionScreen phase="signing-in" />
  }

  if (authPhase === "welcome") {
    return <AuthTransitionScreen phase="welcome" userName={user?.userName} />
  }

  if (!isAuthenticated) {
    return (
      <GithubLoginPage
        loginUrl={env.githubLoginUrl}
        error={authError}
      />
    )
  }

  if (activePage === "logout") {
    return (
      <LogoutPage
        onCancel={() => setActivePage("home")}
        onLoggedOut={() => {
          setIsAuthenticated(false)
          setUser(null)
          setAuthError(null)
          setAuthPhase("idle")
          setActivePage("home")
        }}
        onError={(message) => setAuthError(message)}
      />
    )
  }

  return (
    <main className="mx-auto flex w-full max-w-6xl flex-col gap-6 px-4 py-8 sm:px-6">
      <section className="space-y-2">
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div>
            <h1 className="text-3xl font-semibold tracking-tight">Case Manager frontend starter</h1>
            <p className="text-muted-foreground">
              Minimal shadcn template with typed DTOs, API client, and OpenAPI integration.
            </p>
          </div>
          <Button variant="outline" onClick={() => setActivePage("logout")}>
            Logout
          </Button>
        </div>
        <div className="flex flex-wrap gap-2 pt-2">
          <Badge variant="outline">API base: {env.apiBaseUrl}</Badge>
          <Badge variant="outline">OpenAPI: {env.openapiUrl}</Badge>
        </div>
      </section>

      <OpenApiPanel />
      <Separator />

      <section className="grid gap-4 md:grid-cols-2">
        <CreateTicketForm />
        <CreateAssignmentGroupForm />
      </section>

      <Card>
        <CardHeader>
          <CardTitle>Ticket list preview</CardTitle>
          <CardDescription>
            Quick smoke-test against <code>GET /api/v1/tickets</code>.
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-2">
          {loading && <p className="text-sm text-muted-foreground">Loading tickets...</p>}
          {dataError && <p className="text-sm text-destructive">Error: {dataError}</p>}
          {!loading && !dataError && tickets.length === 0 && (
            <p className="text-sm text-muted-foreground">No tickets yet.</p>
          )}
          {!loading && !dataError && tickets.map((ticket) => (
            <div
              key={ticket.id}
              className="flex items-center justify-between rounded-md border border-border p-3"
            >
              <div>
                <p className="font-medium">{ticket.number ?? "NEW"}</p>
                <p className="text-sm text-muted-foreground">{ticket.title}</p>
              </div>
              <Badge>{ticket.status}</Badge>
            </div>
          ))}
        </CardContent>
      </Card>
    </main>
  )
}

export default App
