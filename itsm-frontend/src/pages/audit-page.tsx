import { useState } from "react"
import { useQuery } from "@tanstack/react-query"
import { caseManagerApi } from "@/api/case-manager-client"
import { PageHeader } from "@/components/page-header"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"

export const AuditPage = () => {
  const [ticketId, setTicketId] = useState("")
  const [submittedTicketId, setSubmittedTicketId] = useState("")

  const auditQuery = useQuery({
    queryKey: ["audit", submittedTicketId],
    queryFn: () =>
      submittedTicketId
        ? caseManagerApi.audit.byTicket(submittedTicketId, { size: 50 })
        : caseManagerApi.audit.list({ size: 50 }),
  })

  return (
    <div className="space-y-6">
      <PageHeader title="Audit Logs" description="Review ticket actions and recorded field changes." />
      <Card>
        <CardHeader className="flex flex-row items-center justify-between">
          <CardTitle>Audit timeline</CardTitle>
          <div className="flex w-full max-w-md gap-2">
            <Input
              placeholder="Optional ticket UUID"
              value={ticketId}
              onChange={(event) => setTicketId(event.target.value)}
            />
            <Button onClick={() => setSubmittedTicketId(ticketId.trim())}>Apply</Button>
          </div>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Created</TableHead>
                <TableHead>Actor</TableHead>
                <TableHead>Action</TableHead>
                <TableHead>Field</TableHead>
                <TableHead>Old</TableHead>
                <TableHead>New</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {auditQuery.data?.items.map((entry) => (
                <TableRow key={entry.id}>
                  <TableCell>{new Date(entry.createdAt).toLocaleString()}</TableCell>
                  <TableCell>{entry.actorEmail}</TableCell>
                  <TableCell>{entry.ticketAction}</TableCell>
                  <TableCell>{entry.field ?? "-"}</TableCell>
                  <TableCell>{entry.oldValue ?? "-"}</TableCell>
                  <TableCell>{entry.newValue ?? "-"}</TableCell>
                </TableRow>
              ))}
              {auditQuery.data?.items.length === 0 && (
                <TableRow>
                  <TableCell colSpan={6} className="py-8 text-center text-muted-foreground">
                    No audit records found.
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
