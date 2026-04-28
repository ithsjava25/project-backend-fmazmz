import { useMemo } from "react"
import { useQuery } from "@tanstack/react-query"
import { AlertTriangle, CheckCircle2, Clock3, Layers3 } from "lucide-react"
import { Link } from "react-router-dom"
import { caseManagerApi } from "@/api/case-manager-client"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { PageHeader } from "@/components/page-header"
import { formatDateTime, formatEnumLabel } from "@/lib/format"

export const DashboardPage = () => {
  const { data: ticketsPage } = useQuery({
    queryKey: ["tickets", "dashboard"],
    queryFn: () => caseManagerApi.tickets.list({ size: 50 }),
  })

  const { data: groups } = useQuery({
    queryKey: ["groups", "dashboard"],
    queryFn: caseManagerApi.assignmentGroups.list,
  })

  const stats = useMemo(() => {
    const tickets = ticketsPage?.items ?? []
    const openCount = tickets.filter((ticket) => !["RESOLVED", "CLOSED"].includes(ticket.status)).length
    const highPriority = tickets.filter((ticket) => ticket.priority === "P1" || ticket.priority === "P2").length
    return {
      total: tickets.length,
      open: openCount,
      highPriority,
      groups: groups?.length ?? 0,
    }
  }, [groups?.length, ticketsPage?.items])

  const cards = [
    { title: "Tickets", value: stats.total, caption: "Loaded from current queue", icon: Layers3 },
    { title: "Open", value: stats.open, caption: "Not resolved or closed", icon: Clock3 },
    { title: "P1/P2", value: stats.highPriority, caption: "High-priority tickets", icon: AlertTriangle },
    { title: "Groups", value: stats.groups, caption: "Assignment groups", icon: CheckCircle2 },
  ]

  return (
    <>
      <PageHeader
        title="Dashboard"
        description="Current ticket and assignment group summary."
      />
      <section className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {cards.map(({ title, value, caption, icon: Icon }) => (
          <Card key={title} className="overflow-hidden">
            <CardHeader className="flex flex-row items-center justify-between pb-3">
              <CardTitle className="text-sm text-muted-foreground">{title}</CardTitle>
              <div className="rounded-xl border border-border bg-muted p-2 text-foreground">
                <Icon className="size-4" />
              </div>
            </CardHeader>
            <CardContent>
              <div className="text-4xl font-semibold tracking-tight">{value}</div>
              <p className="mt-2 text-xs text-muted-foreground">{caption}</p>
            </CardContent>
          </Card>
        ))}
      </section>
      <Card className="mt-6">
        <CardHeader>
          <CardTitle>Recent tickets</CardTitle>
        </CardHeader>
        <CardContent className="space-y-2">
          {(ticketsPage?.items ?? []).slice(0, 5).map((ticket) => (
            <div key={ticket.id} className="flex items-center justify-between rounded-xl border border-border/60 bg-background/70 p-3 text-sm">
              <div>
                <p className="font-medium">
                  <Link to={`/app/tickets/${ticket.id}`} className="hover:underline">
                    {ticket.number}
                  </Link>
                </p>
                <p className="text-muted-foreground">{ticket.title}</p>
                <p className="text-xs text-muted-foreground">
                  Opened: {formatDateTime(ticket.createdAt)} · Updated: {formatDateTime(ticket.updatedAt)}
                </p>
              </div>
              <span className="text-xs text-muted-foreground">{formatEnumLabel(ticket.status)}</span>
            </div>
          ))}
          {(ticketsPage?.items ?? []).length === 0 && (
            <p className="text-sm text-muted-foreground">No tickets returned by the API.</p>
          )}
        </CardContent>
      </Card>
    </>
  )
}
