import { useState } from "react"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { toast } from "sonner"
import { caseManagerApi } from "@/api/case-manager-client"
import { PageHeader } from "@/components/page-header"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Button } from "@/components/ui/button"

export const AssignmentGroupsPage = () => {
  const queryClient = useQueryClient()
  const [name, setName] = useState("")
  const [description, setDescription] = useState("")
  const [memberIds, setMemberIds] = useState("")

  const groupsQuery = useQuery({
    queryKey: ["assignment-groups"],
    queryFn: caseManagerApi.assignmentGroups.list,
  })

  const createMutation = useMutation({
    mutationFn: () => caseManagerApi.assignmentGroups.create({ name, description }),
    onSuccess: () => {
      setName("")
      setDescription("")
      toast.success("Assignment group created")
      void queryClient.invalidateQueries({ queryKey: ["assignment-groups"] })
    },
    onError: (error: Error) => toast.error(error.message),
  })

  const addMembersMutation = useMutation({
    mutationFn: ({ groupId, users }: { groupId: string; users: string[] }) =>
      caseManagerApi.assignmentGroups.addMembers(groupId, { userIds: users }),
    onSuccess: () => toast.success("Members updated"),
    onError: (error: Error) => toast.error(error.message),
  })

  return (
    <div className="space-y-6">
      <PageHeader title="Assignment Groups" description="Manage resolver groups and their members." />
      <section className="grid gap-4 xl:grid-cols-[1fr_360px]">
        <Card>
          <CardHeader><CardTitle>Groups</CardTitle></CardHeader>
          <CardContent className="space-y-3">
            {groupsQuery.data?.map((group) => (
              <div key={group.id} className="rounded-md border border-border p-3">
                <div className="flex items-start justify-between gap-4">
                  <div>
                    <p className="font-medium">{group.name}</p>
                    <p className="text-sm text-muted-foreground">{group.description ?? "No description"}</p>
                    <p className="mt-1 text-xs text-muted-foreground">
                      Members: {group.memberIds.length}
                    </p>
                  </div>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() =>
                      addMembersMutation.mutate({
                        groupId: group.id,
                        users: memberIds.split(",").map((item) => item.trim()).filter(Boolean),
                      })
                    }
                  >
                    Add Members
                  </Button>
                </div>
              </div>
            ))}
            {groupsQuery.data?.length === 0 && (
              <p className="rounded-xl border border-dashed border-border p-6 text-sm text-muted-foreground">
                No assignment groups returned by the API.
              </p>
            )}
            <Input
              placeholder="CSV user IDs to add (used by selected group button)"
              value={memberIds}
              onChange={(event) => setMemberIds(event.target.value)}
            />
          </CardContent>
        </Card>

        <Card>
          <CardHeader><CardTitle>Create group</CardTitle></CardHeader>
          <CardContent className="space-y-3">
            <Input value={name} onChange={(event) => setName(event.target.value)} placeholder="Group name" />
            <Textarea
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              rows={4}
              placeholder="Description"
            />
            <Button onClick={() => createMutation.mutate()} disabled={createMutation.isPending || !name}>
              {createMutation.isPending ? "Creating..." : "Create group"}
            </Button>
          </CardContent>
        </Card>
      </section>
    </div>
  )
}
