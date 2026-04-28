import { useMemo, useState } from "react"
import { useQuery } from "@tanstack/react-query"
import { Link } from "react-router-dom"
import { caseManagerApi } from "@/api/case-manager-client"
import { PageHeader } from "@/components/page-header"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { buttonVariants } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { formatEnumLabel } from "@/lib/format"

export const TicketsPage = () => {
  const [query, setQuery] = useState("")

  const ticketsQuery = useQuery({
    queryKey: ["tickets", "list"],
    queryFn: () => caseManagerApi.tickets.list({ size: 50 }),
  })

  const filteredTickets = useMemo(() => {
    const tickets = ticketsQuery.data?.items ?? []
    if (!query.trim()) {
      return tickets
    }
    const normalized = query.toLowerCase()
    return tickets.filter((ticket) =>
      [ticket.number, ticket.title, ticket.status, ticket.priority].join(" ").toLowerCase().includes(normalized),
    )
  }, [query, ticketsQuery.data?.items])

  return (
    <div className="space-y-6">
      <PageHeader
        title="Tickets"
        description="Create, review, and update incidents."
      />
      <section className="grid gap-4 xl:grid-cols-[1fr_360px]">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle>Ticket queue</CardTitle>
            <div className="flex items-center gap-2">
              <Input
                className="max-w-xs"
                placeholder="Filter by number/title/status"
                value={query}
                onChange={(event) => setQuery(event.target.value)}
              />
              <Link to="/app/tickets/new" className={buttonVariants()}>
                New incident
              </Link>
            </div>
          </CardHeader>
          <CardContent>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Number</TableHead>
                  <TableHead>Title</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Priority</TableHead>
                  <TableHead>Group</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredTickets.map((ticket) => (
                  <TableRow key={ticket.id}>
                    <TableCell>
                      <Link to={`/app/tickets/${ticket.id}`} className="font-medium text-primary hover:underline">
                        {ticket.number}
                      </Link>
                    </TableCell>
                    <TableCell>{ticket.title}</TableCell>
                    <TableCell><Badge variant="outline">{formatEnumLabel(ticket.status)}</Badge></TableCell>
                    <TableCell>{ticket.priority}</TableCell>
                    <TableCell>{ticket.assignmentGroupName ?? "-"}</TableCell>
                  </TableRow>
                ))}
                {filteredTickets.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={5} className="py-8 text-center text-muted-foreground">
                      No tickets found.
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Incident workspace</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4 text-sm text-muted-foreground">
            <p>
              Open an incident to work in the full ticket view with operational fields, editable sections by
              access level, and a comments timeline for agent collaboration.
            </p>
            <Link to="/app/tickets/new" className={buttonVariants({ variant: "outline", className: "w-full" })}>
              Open new incident workspace
            </Link>
          </CardContent>
        </Card>
      </section>
    </div>
  )
}
