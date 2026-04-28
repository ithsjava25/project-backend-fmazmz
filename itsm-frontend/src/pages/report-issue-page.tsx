import { useState } from "react"
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { useNavigate } from "react-router-dom"
import { toast } from "sonner"
import { caseManagerApi } from "@/api/case-manager-client"
import { PageHeader } from "@/components/page-header"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"

export const ReportIssuePage = () => {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [title, setTitle] = useState("")
  const [description, setDescription] = useState("")

  const createMutation = useMutation({
    mutationFn: () =>
      caseManagerApi.tickets.create({
        title: title.trim(),
        description: description.trim(),
        type: "INCIDENT",
      }),
    onSuccess: (createdTicket) => {
      toast.success("Issue reported")
      void queryClient.invalidateQueries({ queryKey: ["tickets"] })
      void queryClient.invalidateQueries({ queryKey: ["tickets", "my-reported"] })
      navigate(`/app/tickets/${createdTicket.id}`)
    },
    onError: (error: Error) => toast.error(error.message),
  })

  return (
    <div className="space-y-6">
      <PageHeader
        title="Report an Issue"
        description="Submit an incident to the service desk and track updates in your reported incidents list."
      />
      <Card>
        <CardHeader className="border-b border-border">
          <CardTitle className="text-base">Issue details</CardTitle>
        </CardHeader>
        <CardContent className="space-y-6 pt-6">
          <div className="space-y-1">
            <p className="text-xs uppercase tracking-wide text-muted-foreground">Title</p>
            <Input
              value={title}
              onChange={(event) => setTitle(event.target.value)}
              placeholder="Brief summary of the issue"
              className="h-11"
            />
          </div>
          <div className="space-y-1">
            <p className="text-xs uppercase tracking-wide text-muted-foreground">Description</p>
            <Textarea
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              placeholder="Describe what happened, who is affected, and any error details."
              className="min-h-40"
            />
          </div>
          <div className="flex items-center justify-end gap-2 border-t border-border pt-4">
            <Button variant="outline" onClick={() => navigate("/app/my-reported-tickets")}>
              Cancel
            </Button>
            <Button
              onClick={() => createMutation.mutate()}
              disabled={createMutation.isPending || !title.trim() || !description.trim()}
            >
              {createMutation.isPending ? "Submitting..." : "Submit issue"}
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
