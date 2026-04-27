import { useEffect, useState } from "react"
import { Badge } from "@/components/ui/badge"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Separator } from "@/components/ui/separator"
import { OpenApiPanel } from "@/components/openapi-panel"
import { CreateAssignmentGroupForm } from "@/components/create-assignment-group-form"
import { CreateTicketForm } from "@/components/create-ticket-form"
import { caseManagerApi } from "@/api/case-manager-client"
import { env } from "@/config/env"
import type { TicketResponse } from "@/types/api"

function App() {
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [tickets, setTickets] = useState<TicketResponse[]>([])

  useEffect(() => {
    const loadTickets = async () => {
      try {
        const page = await caseManagerApi.tickets.list()
        setTickets(page.items)
      } catch (requestError) {
        const message = requestError instanceof Error ? requestError.message : "Unexpected error"
        setError(message)
      } finally {
        setLoading(false)
      }
    }

    void loadTickets()
  }, [])

  return (
    <main className="mx-auto flex w-full max-w-6xl flex-col gap-6 px-4 py-8 sm:px-6">
      <section className="space-y-2">
        <h1 className="text-3xl font-semibold tracking-tight">Case Manager frontend starter</h1>
        <p className="text-muted-foreground">
          Minimal shadcn template with typed DTOs, API client, and OpenAPI integration.
        </p>
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
          {error && <p className="text-sm text-destructive">Error: {error}</p>}
          {!loading && !error && tickets.length === 0 && (
            <p className="text-sm text-muted-foreground">No tickets yet.</p>
          )}
          {!loading && !error && tickets.map((ticket) => (
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
