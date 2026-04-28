import { useEffect, useState } from "react"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { Link, useParams } from "react-router-dom"
import { toast } from "sonner"
import { caseManagerApi } from "@/api/case-manager-client"
import { PageHeader } from "@/components/page-header"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Button, buttonVariants } from "@/components/ui/button"

export const AssignmentGroupDetailsPage = () => {
  const { groupId = "" } = useParams()
  const queryClient = useQueryClient()
  const [memberIdsInput, setMemberIdsInput] = useState("")

  const groupQuery = useQuery({
    queryKey: ["assignment-group", groupId],
    queryFn: () => caseManagerApi.assignmentGroups.getById(groupId),
    enabled: Boolean(groupId),
  })

  const [name, setName] = useState("")
  const [description, setDescription] = useState("")

  const group = groupQuery.data

  useEffect(() => {
    if (!group) {
      return
    }
    setName(group.name)
    setDescription(group.description ?? "")
  }, [group])

  const updateMutation = useMutation({
    mutationFn: () => caseManagerApi.assignmentGroups.update(groupId, { name, description }),
    onSuccess: () => {
      toast.success("Group updated")
      void queryClient.invalidateQueries({ queryKey: ["assignment-group", groupId] })
      void queryClient.invalidateQueries({ queryKey: ["assignment-groups"] })
    },
    onError: (error: Error) => toast.error(error.message),
  })

  const addMembersMutation = useMutation({
    mutationFn: (userIds: string[]) => caseManagerApi.assignmentGroups.addMembers(groupId, { userIds }),
    onSuccess: () => {
      toast.success("Members added")
      setMemberIdsInput("")
      void queryClient.invalidateQueries({ queryKey: ["assignment-group", groupId] })
      void queryClient.invalidateQueries({ queryKey: ["assignment-groups"] })
    },
    onError: (error: Error) => toast.error(error.message),
  })

  const removeMemberMutation = useMutation({
    mutationFn: (userId: string) => caseManagerApi.assignmentGroups.removeMember(groupId, userId),
    onSuccess: () => {
      toast.success("Member removed")
      void queryClient.invalidateQueries({ queryKey: ["assignment-group", groupId] })
      void queryClient.invalidateQueries({ queryKey: ["assignment-groups"] })
    },
    onError: (error: Error) => toast.error(error.message),
  })

  const parsedMemberIds = memberIdsInput
    .split(/[\s,]+/)
    .map((item) => item.trim())
    .filter(Boolean)

  if (!group) {
    return <p className="text-sm text-muted-foreground">Loading assignment group...</p>
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title={group.name}
        description="Edit group details, review members, and manage membership."
      />
      <div>
        <Link to="/app/assignment-groups" className={buttonVariants({ variant: "outline", size: "sm" })}>
          Back to groups
        </Link>
      </div>
      <section className="grid gap-4 xl:grid-cols-[1fr_420px]">
        <Card>
          <CardHeader className="border-b border-border">
            <CardTitle>Group details</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4 pt-5">
            <div className="space-y-1">
              <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">Group name</p>
              <Input value={name} onChange={(event) => setName(event.target.value)} />
            </div>
            <div className="space-y-1">
              <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">Description</p>
              <Textarea value={description} onChange={(event) => setDescription(event.target.value)} rows={5} />
            </div>
            <div className="flex justify-end border-t border-border pt-4">
              <Button disabled={updateMutation.isPending || !name.trim()} onClick={() => updateMutation.mutate()}>
                {updateMutation.isPending ? "Saving..." : "Save group"}
              </Button>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="border-b border-border">
            <CardTitle>Members</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4 pt-5">
            <div className="space-y-1">
              <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
                Add members (user IDs)
              </p>
              <Textarea
                rows={4}
                placeholder="uuid-1, uuid-2"
                value={memberIdsInput}
                onChange={(event) => setMemberIdsInput(event.target.value)}
              />
              <p className="text-xs text-muted-foreground">Parsed IDs: {parsedMemberIds.length}</p>
            </div>
            <Button
              className="w-full"
              disabled={addMembersMutation.isPending || parsedMemberIds.length === 0}
              onClick={() => addMembersMutation.mutate(parsedMemberIds)}
            >
              {addMembersMutation.isPending ? "Adding..." : "Add members"}
            </Button>
            <div className="space-y-2 border-t border-border pt-4">
              {group.memberIds.map((memberId) => (
                <div key={memberId} className="flex items-center justify-between rounded-md border border-border p-2 text-sm">
                  <span className="truncate text-muted-foreground">{memberId}</span>
                  <Button
                    variant="outline"
                    size="sm"
                    disabled={removeMemberMutation.isPending}
                    onClick={() => removeMemberMutation.mutate(memberId)}
                  >
                    Remove
                  </Button>
                </div>
              ))}
              {group.memberIds.length === 0 && (
                <p className="text-sm text-muted-foreground">No members yet.</p>
              )}
            </div>
          </CardContent>
        </Card>
      </section>
    </div>
  )
}

