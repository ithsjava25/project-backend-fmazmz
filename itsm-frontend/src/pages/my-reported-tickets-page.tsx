import { useQuery } from "@tanstack/react-query"
import { Link } from "react-router-dom"
import { caseManagerApi } from "@/api/case-manager-client"
import { PageHeader } from "@/components/page-header"
import { Badge } from "@/components/ui/badge"
import { buttonVariants } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { useAuth } from "@/lib/auth-context"
import { formatDateTime, formatEnumLabel } from "@/lib/format"

export const MyReportedTicketsPage = () => {
  const { user } = useAuth()
  const isViewer = user.roles.includes("VIEWER")

  const ticketsQuery = useQuery({
    queryKey: ["tickets", "my-reported", user.id],
    queryFn: () => caseManagerApi.tickets.listByRequester(user.id, { size: 100 }),
  })

  const tickets = ticketsQuery.data?.items ?? []

  return (
    <div className="space-y-6">
      <PageHeader
        title="My Reported Incidents"
        description="Track your submitted incidents and follow updates from service teams."
      />
      <Card>
        <CardHeader className="flex flex-row items-center justify-between">
          <CardTitle>My incidents</CardTitle>
          {!isViewer && (
            <Link to="/app/report-issue" className={buttonVariants()}>
              Report an issue
            </Link>
          )}
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Number</TableHead>
                <TableHead>Title</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Priority</TableHead>
                <TableHead>Opened at</TableHead>
                <TableHead>Updated at</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {tickets.map((ticket) => (
                <TableRow key={ticket.id}>
                  <TableCell>
                    <Link to={`/app/tickets/${ticket.id}`} className="font-medium hover:underline">
                      {ticket.number}
                    </Link>
                  </TableCell>
                  <TableCell>{ticket.title}</TableCell>
                  <TableCell>
                    <Badge variant="outline">{formatEnumLabel(ticket.status)}</Badge>
                  </TableCell>
                  <TableCell>{ticket.priority}</TableCell>
                  <TableCell>{formatDateTime(ticket.createdAt)}</TableCell>
                  <TableCell>{formatDateTime(ticket.updatedAt)}</TableCell>
                </TableRow>
              ))}
              {tickets.length === 0 && (
                <TableRow>
                  <TableCell colSpan={6} className="py-8 text-center text-muted-foreground">
                    No incidents reported yet.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  )
}
