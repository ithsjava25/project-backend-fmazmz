import { useState } from "react"
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { useNavigate } from "react-router-dom"
import { toast } from "sonner"
import { caseManagerApi } from "@/api/case-manager-client"
import { PageHeader } from "@/components/page-header"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Button } from "@/components/ui/button"

export const AssignmentGroupCreatePage = () => {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [name, setName] = useState("")
  const [description, setDescription] = useState("")

  const createMutation = useMutation({
    mutationFn: () => caseManagerApi.assignmentGroups.create({ name, description }),
    onSuccess: (group) => {
      toast.success("Assignment group created")
      void queryClient.invalidateQueries({ queryKey: ["assignment-groups"] })
      navigate(`/app/assignment-groups/${group.id}`)
    },
    onError: (error: Error) => toast.error(error.message),
  })

  return (
    <div className="space-y-6">
      <PageHeader
        title="Create Assignment Group"
        description="Create a new resolver group with clear ownership scope."
      />
      <Card className="mx-auto w-full max-w-2xl">
        <CardHeader className="border-b border-border">
          <CardTitle>Group details</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4 pt-5">
          <div className="space-y-1">
            <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">Group name</p>
            <Input
              value={name}
              onChange={(event) => setName(event.target.value)}
              placeholder="e.g. L2 Platform Support"
            />
          </div>
          <div className="space-y-1">
            <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">Description</p>
            <Textarea
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              rows={6}
              placeholder="Scope, responsibilities, and escalation criteria."
            />
          </div>
          <div className="flex justify-end gap-2 border-t border-border pt-4">
            <Button variant="outline" onClick={() => navigate("/app/assignment-groups")}>
              Cancel
            </Button>
            <Button disabled={createMutation.isPending || !name.trim()} onClick={() => createMutation.mutate()}>
              {createMutation.isPending ? "Creating..." : "Create group"}
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

