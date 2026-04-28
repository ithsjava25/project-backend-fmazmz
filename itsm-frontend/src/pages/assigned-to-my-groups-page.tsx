import { useMemo } from "react"
import { useQuery } from "@tanstack/react-query"
import { Link } from "react-router-dom"
import { caseManagerApi } from "@/api/case-manager-client"
import { PageHeader } from "@/components/page-header"
import { Badge } from "@/components/ui/badge"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { useAuth } from "@/lib/auth-context"
import { formatDateTime, formatEnumLabel } from "@/lib/format"

export const AssignedToMyGroupsPage = () => {
  const { user } = useAuth()

  const ticketsQuery = useQuery({
    queryKey: ["tickets", "assigned-to-my-groups", user.id],
    queryFn: async () => {
      const groups = await caseManagerApi.assignmentGroups.list()
      const myGroups = groups.filter((group) => group.memberIds.includes(user.id))
      if (myGroups.length === 0) {
        return []
      }

      const pages = await Promise.all(
        myGroups.map((group) => caseManagerApi.tickets.listByAssignmentGroup(group.id, { size: 100 })),
      )

      const deduped = new Map<string, (typeof pages)[number]["items"][number]>()
      pages.flatMap((page) => page.items).forEach((ticket) => {
        deduped.set(ticket.id, ticket)
      })

      return Array.from(deduped.values())
    },
  })

  const tickets = ticketsQuery.data ?? []
  const sortedTickets = useMemo(
    () =>
      [...tickets].sort(
        (a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime(),
      ),
    [tickets],
  )

  return (
    <div className="space-y-6">
      <PageHeader
        title="Assigned To My Groups"
        description="Incidents assigned to any assignment group where you are a member."
      />
      <Card>
        <CardHeader>
          <CardTitle>Group queue</CardTitle>
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
                <TableHead>Opened at</TableHead>
                <TableHead>Updated at</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {sortedTickets.map((ticket) => (
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
                  <TableCell>{formatDateTime(ticket.createdAt)}</TableCell>
                  <TableCell>{formatDateTime(ticket.updatedAt)}</TableCell>
                </TableRow>
              ))}
              {sortedTickets.length === 0 && (
                <TableRow>
                  <TableCell colSpan={7} className="py-8 text-center text-muted-foreground">
                    No incidents are assigned to your groups.
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

