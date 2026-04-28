import { useQuery } from "@tanstack/react-query"
import { Link } from "react-router-dom"
import { caseManagerApi } from "@/api/case-manager-client"
import { PageHeader } from "@/components/page-header"
import { Badge } from "@/components/ui/badge"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { useAuth } from "@/lib/auth-context"
import { formatEnumLabel } from "@/lib/format"

export const AssignedToMePage = () => {
  const { user } = useAuth()
  const ticketsQuery = useQuery({
    queryKey: ["tickets", "assigned-to-me", user.id],
    queryFn: () => caseManagerApi.tickets.listByAssignee(user.id, { size: 100 }),
  })

  const tickets = ticketsQuery.data?.items ?? []

  return (
    <div className="space-y-6">
      <PageHeader
        title="Assigned To Me"
        description="Tickets currently assigned to your user account."
      />
      <Card>
        <CardHeader>
          <CardTitle>My queue</CardTitle>
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
                  <TableCell>{ticket.assignmentGroupName ?? "-"}</TableCell>
                </TableRow>
              ))}
              {tickets.length === 0 && (
                <TableRow>
                  <TableCell colSpan={5} className="py-8 text-center text-muted-foreground">
                    No tickets are currently assigned to you.
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

