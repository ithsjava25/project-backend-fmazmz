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
import { formatDateTime, formatEnumLabel } from "@/lib/format"
import type { Priority, TicketStatus } from "@/types/api"

const priorityOptions: Priority[] = ["P1", "P2", "P3", "P4", "P5"]
const statusOptions: TicketStatus[] = ["OPEN", "ASSIGNED", "WORK_IN_PROGRESS", "AWAITING_USER_INFO", "RESOLVED", "CLOSED"]

export const TicketsPage = () => {
  const [numberFilter, setNumberFilter] = useState("")
  const [titleFilter, setTitleFilter] = useState("")
  const [statusFilter, setStatusFilter] = useState("")
  const [priorityFilter, setPriorityFilter] = useState("")
  const [groupFilter, setGroupFilter] = useState("")

  const ticketsQuery = useQuery({
    queryKey: ["tickets", "list"],
    queryFn: () => caseManagerApi.tickets.list({ size: 50 }),
  })

  const groupsQuery = useQuery({
    queryKey: ["assignment-groups", "ticket-filters"],
    queryFn: caseManagerApi.assignmentGroups.list,
  })

  const filteredTickets = useMemo(() => {
    const tickets = ticketsQuery.data?.items ?? []
    const normalizedNumber = numberFilter.trim().toLowerCase()
    const normalizedTitle = titleFilter.trim().toLowerCase()
    return tickets.filter((ticket) => {
      const matchesNumber = !normalizedNumber || ticket.number.toLowerCase().includes(normalizedNumber)
      const matchesTitle = !normalizedTitle || ticket.title.toLowerCase().includes(normalizedTitle)
      const matchesStatus = !statusFilter || ticket.status === statusFilter
      const matchesPriority = !priorityFilter || ticket.priority === priorityFilter
      const matchesGroup = !groupFilter || ticket.assignmentGroupId === groupFilter
      return matchesNumber && matchesTitle && matchesStatus && matchesPriority && matchesGroup
    })
  }, [numberFilter, titleFilter, statusFilter, priorityFilter, groupFilter, ticketsQuery.data?.items])

  return (
    <div className="space-y-6">
      <PageHeader
        title="Incidents"
        description="Create, review, and update incidents."
      />
      <section>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle>Incident queue</CardTitle>
            <Link to="/app/tickets/new" className={buttonVariants()}>
              New incident
            </Link>
          </CardHeader>
          <CardContent>
            <div className="mb-4 grid gap-3 rounded-lg border border-border p-3 md:grid-cols-2 xl:grid-cols-5">
              <Input
                placeholder="Number"
                value={numberFilter}
                onChange={(event) => setNumberFilter(event.target.value)}
              />
              <Input
                placeholder="Title"
                value={titleFilter}
                onChange={(event) => setTitleFilter(event.target.value)}
              />
              <select
                className="h-10 rounded-md border border-input bg-background px-3 py-2 text-sm"
                value={statusFilter}
                onChange={(event) => setStatusFilter(event.target.value)}
              >
                <option value="">All statuses</option>
                {statusOptions.map((status) => (
                  <option key={status} value={status}>
                    {formatEnumLabel(status)}
                  </option>
                ))}
              </select>
              <select
                className="h-10 rounded-md border border-input bg-background px-3 py-2 text-sm"
                value={priorityFilter}
                onChange={(event) => setPriorityFilter(event.target.value)}
              >
                <option value="">All priorities</option>
                {priorityOptions.map((priority) => (
                  <option key={priority} value={priority}>
                    {priority}
                  </option>
                ))}
              </select>
              <select
                className="h-10 rounded-md border border-input bg-background px-3 py-2 text-sm"
                value={groupFilter}
                onChange={(event) => setGroupFilter(event.target.value)}
              >
                <option value="">All groups</option>
                {(groupsQuery.data ?? []).map((group) => (
                  <option key={group.id} value={group.id}>
                    {group.name}
                  </option>
                ))}
              </select>
            </div>
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
                    <TableCell>{formatDateTime(ticket.createdAt)}</TableCell>
                    <TableCell>{formatDateTime(ticket.updatedAt)}</TableCell>
                  </TableRow>
                ))}
                {filteredTickets.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={7} className="py-8 text-center text-muted-foreground">
                      No incidents found.
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      </section>
    </div>
  )
}
