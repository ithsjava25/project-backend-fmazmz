import { useMemo, useState } from "react"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { useNavigate } from "react-router-dom"
import { toast } from "sonner"
import { caseManagerApi } from "@/api/case-manager-client"
import { PageHeader } from "@/components/page-header"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { useAuth } from "@/lib/auth-context"
import type { CreateTicketRequest } from "@/types/api"

const defaultForm: Pick<CreateTicketRequest, "title" | "description" | "assignmentGroupId"> = {
  title: "",
  description: "",
}

export const TicketCreatePage = () => {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { user } = useAuth()
  const [form, setForm] = useState(defaultForm)

  const groupsQuery = useQuery({
    queryKey: ["groups", "ticket-create"],
    queryFn: caseManagerApi.assignmentGroups.list,
  })

  const createMutation = useMutation({
    mutationFn: caseManagerApi.tickets.create,
    onSuccess: (createdTicket) => {
      toast.success("Incident created")
      void queryClient.invalidateQueries({ queryKey: ["tickets"] })
      navigate(`/app/tickets/${createdTicket.id}`)
    },
    onError: (error: Error) => toast.error(error.message),
  })

  const assignmentGroups = useMemo(() => groupsQuery.data ?? [], [groupsQuery.data])

  return (
    <div className="space-y-6">
      <PageHeader
        title="New incident"
        description="Create an incident in the same workspace agents use to investigate and collaborate."
      />

      <Card>
        <CardHeader className="border-b border-border">
          <CardTitle className="text-base">Incident details</CardTitle>
        </CardHeader>
        <CardContent className="space-y-6 pt-6">
          <div className="grid gap-4 md:grid-cols-2">
            <div className="space-y-1">
              <p className="text-xs uppercase tracking-wide text-muted-foreground">Incident number</p>
              <Input value="Auto-generated after save" disabled className="bg-muted text-muted-foreground" />
            </div>
            <div className="space-y-1">
              <p className="text-xs uppercase tracking-wide text-muted-foreground">Reported by</p>
              <Input value={user.email} disabled className="bg-muted text-muted-foreground" />
            </div>
            <div className="space-y-1">
              <p className="text-xs uppercase tracking-wide text-muted-foreground">Assigned to</p>
              <Input value="Unassigned" disabled className="bg-muted text-muted-foreground" />
            </div>
            <div className="space-y-1">
              <p className="text-xs uppercase tracking-wide text-muted-foreground">Assignment group</p>
              <select
                className="h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                value={form.assignmentGroupId ?? ""}
                onChange={(event) =>
                  setForm((current) => ({ ...current, assignmentGroupId: event.target.value || undefined }))
                }
              >
                <option value="">Select group</option>
                {assignmentGroups.map((group) => (
                  <option key={group.id} value={group.id}>
                    {group.name}
                  </option>
                ))}
              </select>
            </div>
            <div className="space-y-1">
              <p className="text-xs uppercase tracking-wide text-muted-foreground">Type</p>
              <Input value="INCIDENT" disabled className="bg-muted text-muted-foreground" />
            </div>
          </div>

          <div className="space-y-1">
            <p className="text-xs uppercase tracking-wide text-muted-foreground">Title</p>
            <Input
              value={form.title}
              onChange={(event) => setForm((current) => ({ ...current, title: event.target.value }))}
              placeholder="Brief summary of the incident"
              className="h-11"
            />
          </div>

          <div className="space-y-1">
            <p className="text-xs uppercase tracking-wide text-muted-foreground">Description</p>
            <Textarea
              value={form.description}
              onChange={(event) => setForm((current) => ({ ...current, description: event.target.value }))}
              placeholder="Describe impact, observed behavior, and any troubleshooting already performed."
              className="min-h-36"
            />
          </div>

          <div className="flex items-center justify-between border-t border-border pt-4">
            <p className="text-xs text-muted-foreground">Comments timeline appears after the incident is created.</p>
            <div className="flex gap-2">
              <Button variant="outline" onClick={() => navigate("/app/tickets")}>
                Cancel
              </Button>
              <Button
                onClick={() => createMutation.mutate({ ...form, type: "INCIDENT" })}
                disabled={createMutation.isPending || !form.title.trim() || !form.description.trim()}
              >
                {createMutation.isPending ? "Creating..." : "Create incident"}
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
