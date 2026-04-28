import { useEffect, useState } from "react"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { useParams } from "react-router-dom"
import { toast } from "sonner"
import { caseManagerApi } from "@/api/case-manager-client"
import { PageHeader } from "@/components/page-header"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Textarea } from "@/components/ui/textarea"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { useAuth } from "@/lib/auth-context"
import { formatDateTime, formatEnumLabel } from "@/lib/format"
import type { CommentVisibility, Priority, TicketStatus } from "@/types/api"

export const TicketDetailsPage = () => {
  const { ticketId = "" } = useParams()
  const queryClient = useQueryClient()
  const { user } = useAuth()
  const [comment, setComment] = useState("")
  const [visibility, setVisibility] = useState<CommentVisibility>("PUBLIC")
  const [draftTitle, setDraftTitle] = useState("")
  const [draftDescription, setDraftDescription] = useState("")
  const [selectedPriority, setSelectedPriority] = useState<Priority>("P3")
  const [selectedStatus, setSelectedStatus] = useState<TicketStatus>("OPEN")
  const [selectedAssignmentGroup, setSelectedAssignmentGroup] = useState("")
  const [selectedAssignee, setSelectedAssignee] = useState("")
  const [transitionTouched, setTransitionTouched] = useState(false)
  const [activityTab, setActivityTab] = useState<"comments" | "resolution">("comments")
  const [resolutionNote, setResolutionNote] = useState("")
  const [attachmentsOpen, setAttachmentsOpen] = useState(false)
  const [attachmentFile, setAttachmentFile] = useState<File | null>(null)

  const ticketQuery = useQuery({
    queryKey: ["ticket", ticketId],
    queryFn: () => caseManagerApi.tickets.getById(ticketId),
    enabled: Boolean(ticketId),
  })

  const commentMutation = useMutation({
    mutationFn: () => caseManagerApi.tickets.comment(ticketId, { visibility, comment }),
    onSuccess: () => {
      setComment("")
      toast.success("Comment added")
      void queryClient.invalidateQueries({ queryKey: ["ticket", ticketId] })
    },
    onError: (error: Error) => toast.error(error.message),
  })

  const groupsQuery = useQuery({
    queryKey: ["groups", "ticket-details", ticketId],
    queryFn: caseManagerApi.assignmentGroups.list,
  })

  const selectedGroup = (groupsQuery.data ?? []).find((group) => group.id === selectedAssignmentGroup)
  const selectedGroupMemberIds = selectedGroup?.memberIds ?? []

  const assigneesQuery = useQuery({
    queryKey: ["group-members", selectedAssignmentGroup, selectedGroupMemberIds.join(",")],
    queryFn: () => caseManagerApi.users.lookupByIds(selectedGroupMemberIds),
    enabled: Boolean(selectedAssignmentGroup) && selectedGroupMemberIds.length > 0,
  })

  const requesterQuery = useQuery({
    queryKey: ["ticket-requester", ticketQuery.data?.requesterId],
    queryFn: async () => {
      const requesterId = ticketQuery.data?.requesterId
      if (!requesterId) {
        return null
      }
      const users = await caseManagerApi.users.lookupByIds([requesterId])
      return users[0] ?? null
    },
    enabled: Boolean(ticketQuery.data?.requesterId),
  })

  const commentAuthorIds = Array.from(new Set((ticketQuery.data?.comments ?? []).map((item) => item.authorId)))
  const commentAuthorsQuery = useQuery({
    queryKey: ["ticket-comment-authors", ticketId, commentAuthorIds.join(",")],
    queryFn: () => caseManagerApi.users.lookupByIds(commentAuthorIds),
    enabled: commentAuthorIds.length > 0,
  })

  const saveMutation = useMutation({
    mutationFn: async () => {
      const currentTicket = ticketQuery.data
      if (!currentTicket) {
        throw new Error("Ticket is not loaded")
      }

      const titleChanged = draftTitle.trim() !== currentTicket.title
      const descriptionChanged = draftDescription.trim() !== currentTicket.description
      const priorityChanged = selectedPriority !== currentTicket.priority
      const transitionChanged =
        selectedStatus !== currentTicket.status ||
        selectedAssignmentGroup !== (currentTicket.assignmentGroupId ?? "") ||
        selectedAssignee !== (currentTicket.assigneeId ?? "") ||
        (requiresResolutionNote && resolutionNote.trim() !== (currentTicket.resolutionNotes ?? ""))
      const assignmentRoutingChanged =
        selectedAssignmentGroup !== (currentTicket.assignmentGroupId ?? "") ||
        selectedAssignee !== (currentTicket.assigneeId ?? "")
      const requiresInternalWorkNote = selectedStatus === "WORK_IN_PROGRESS" || assignmentRoutingChanged
      const requiresPublicUpdate = selectedStatus === "AWAITING_USER_INFO"

      if (!titleChanged && !descriptionChanged && !priorityChanged && !transitionChanged) {
        return false
      }

      if (titleChanged || descriptionChanged) {
        await caseManagerApi.tickets.update(ticketId, {
          title: draftTitle.trim(),
          description: draftDescription.trim(),
        })
      }

      if (priorityChanged) {
        await caseManagerApi.tickets.changePriority(ticketId, {
          priority: selectedPriority,
          internalComment: "Updated via ticket workspace",
        })
      }

      if (transitionChanged) {
        await caseManagerApi.tickets.changeStatus(ticketId, {
          status: selectedStatus,
          assignmentGroup: selectedAssignmentGroup || undefined,
          assignee: selectedAssignee || undefined,
          publicComment: requiresPublicUpdate ? comment.trim() : undefined,
          internalComment: requiresInternalWorkNote ? comment.trim() : undefined,
          resolutionNotes: requiresResolutionNote ? resolutionNote.trim() : undefined,
        })
      }

      if (transitionChanged && comment.trim()) {
        await caseManagerApi.tickets.comment(ticketId, {
          visibility,
          comment: comment.trim(),
        })
      }

      return true
    },
    onSuccess: async (changed) => {
      if (!changed) {
        toast.message("No changes to save")
        return
      }
      setComment("")
      toast.success("Incident saved")
      void queryClient.invalidateQueries({ queryKey: ["ticket", ticketId] })
    },
    onError: (error: Error) => toast.error(error.message),
  })

  const ticket = ticketQuery.data
  const canEdit = user.roles.includes("AGENT") || user.roles.includes("SUPER_AGENT") || user.roles.includes("ADMIN")
  const isStaff = canEdit
  const isViewer = user.roles.includes("VIEWER")
  const canComment = !user.roles.includes("VIEWER")
  const canUploadAttachment = canEdit || user.roles.includes("REPORTER")

  useEffect(() => {
    if (!ticket) {
      return
    }
    setDraftTitle(ticket.title)
    setDraftDescription(ticket.description)
    setSelectedPriority(ticket.priority)
    setSelectedStatus(ticket.status)
    setSelectedAssignmentGroup(ticket.assignmentGroupId ?? "")
    setSelectedAssignee(ticket.assigneeId ?? "")
    setResolutionNote(ticket.resolutionNotes ?? "")
  }, [ticket])

  useEffect(() => {
    if (!selectedAssignmentGroup) {
      setSelectedAssignee("")
      return
    }

    if (selectedAssignee && selectedGroupMemberIds.length > 0 && !selectedGroupMemberIds.includes(selectedAssignee)) {
      setSelectedAssignee("")
    }
  }, [selectedAssignmentGroup, selectedAssignee, selectedGroupMemberIds])

  const requiresAssignment = selectedStatus === "WORK_IN_PROGRESS"
  const requiresPublicComment = selectedStatus === "AWAITING_USER_INFO"
  const requiresResolutionNote = selectedStatus === "RESOLVED"
  const assignmentGroupChanged = selectedAssignmentGroup !== (ticket?.assignmentGroupId ?? "")
  const assigneeChanged = selectedAssignee !== (ticket?.assigneeId ?? "")
  const requiresAssignmentWorkNote = requiresAssignment || assignmentGroupChanged || assigneeChanged
  const hasAssignmentGroup = Boolean(selectedAssignmentGroup)
  const hasAssignee = Boolean(selectedAssignee.trim())
  const hasWipInternalComment = visibility === "INTERNAL" && Boolean(comment.trim())
  const hasRequiredPublicComment = visibility === "PUBLIC" && Boolean(comment.trim())
  const hasResolutionNote = Boolean(resolutionNote.trim())
  const showAssignmentGroupError = requiresAssignment && !hasAssignmentGroup && transitionTouched
  const showAssigneeError = requiresAssignment && !hasAssignee && transitionTouched
  const showWipInternalCommentError = requiresAssignmentWorkNote && !hasWipInternalComment && transitionTouched
  const showPublicCommentError = requiresPublicComment && !hasRequiredPublicComment && transitionTouched
  const showResolutionError = requiresResolutionNote && !hasResolutionNote && transitionTouched
  const commentAuthorEmailById = new Map((commentAuthorsQuery.data ?? []).map((user) => [user.id, user.email]))
  const attachmentsQuery = useQuery({
    queryKey: ["ticket-attachments", ticketId],
    queryFn: () => caseManagerApi.tickets.listAttachments(ticketId),
    enabled: attachmentsOpen && Boolean(ticketId),
  })
  const uploadAttachmentMutation = useMutation({
    mutationFn: async () => {
      if (!attachmentFile) {
        throw new Error("Please select a file first")
      }
      return caseManagerApi.tickets.uploadAttachment(ticketId, attachmentFile)
    },
    onSuccess: async () => {
      setAttachmentFile(null)
      toast.success("Attachment uploaded")
      await queryClient.invalidateQueries({ queryKey: ["ticket-attachments", ticketId] })
      await queryClient.invalidateQueries({ queryKey: ["ticket", ticketId] })
    },
    onError: (error: Error) => toast.error(error.message),
  })

  useEffect(() => {
    if (selectedStatus === "AWAITING_USER_INFO") {
      setVisibility("PUBLIC")
      return
    }
    if (requiresAssignmentWorkNote) {
      setVisibility("INTERNAL")
    }
  }, [selectedStatus, requiresAssignmentWorkNote])

  if (!ticket) {
    return <div className="text-sm text-muted-foreground">Loading ticket details...</div>
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title={`${ticket.number} · Incident`}
        description="Service desk workspace for triage, assignment, and agent collaboration."
      />

      <Card>
        <CardHeader className="flex flex-row items-center justify-between border-b border-border">
          <CardTitle className="text-base">Incident details</CardTitle>
          <div className="flex items-center gap-2">
            <Button type="button" variant="outline" onClick={() => setAttachmentsOpen(true)}>
              Attachments
            </Button>
            {canEdit && (
              <Button
                onClick={() => {
                  setTransitionTouched(true)
                  if (
                    (requiresAssignment && (!hasAssignmentGroup || !hasAssignee || !hasWipInternalComment)) ||
                    (requiresAssignmentWorkNote && !hasWipInternalComment) ||
                    (requiresPublicComment && !hasRequiredPublicComment) ||
                    (requiresResolutionNote && !hasResolutionNote)
                  ) {
                    if (
                      (requiresAssignmentWorkNote && !hasWipInternalComment) ||
                      (requiresPublicComment && !hasRequiredPublicComment)
                    ) {
                      setActivityTab("comments")
                    }
                    if (requiresResolutionNote && !hasResolutionNote) {
                      setActivityTab("resolution")
                    }
                    return
                  }
                  saveMutation.mutate()
                }}
                disabled={saveMutation.isPending || !draftTitle.trim() || !draftDescription.trim()}
              >
                {saveMutation.isPending ? "Saving..." : "Save"}
              </Button>
            )}
          </div>
        </CardHeader>
        <CardContent className="space-y-6 pt-6">
          <div className="grid gap-6 lg:grid-cols-[minmax(0,420px)_minmax(0,420px)] lg:justify-center lg:gap-14">
            <div className="space-y-4">
              <div className="space-y-1">
                <p className="text-xs uppercase tracking-wide text-muted-foreground">Incident number</p>
                <Input value={ticket.number} disabled className="max-w-sm bg-muted text-muted-foreground" />
              </div>
              <div className="space-y-1">
                <p className="text-xs uppercase tracking-wide text-muted-foreground">Reported by</p>
                <Input
                  value={
                    requesterQuery.data
                      ? `${requesterQuery.data.userName} (${requesterQuery.data.email})`
                      : "Loading requester..."
                  }
                  disabled
                  className="max-w-sm bg-muted text-muted-foreground"
                />
              </div>
              <div className="space-y-1">
                <p className="text-xs uppercase tracking-wide text-muted-foreground">Opened at</p>
                <Input value={formatDateTime(ticket.createdAt)} disabled className="max-w-sm bg-muted text-muted-foreground" />
              </div>
              <div className="space-y-1">
                <p className="text-xs uppercase tracking-wide text-muted-foreground">Updated at</p>
                <Input value={formatDateTime(ticket.updatedAt)} disabled className="max-w-sm bg-muted text-muted-foreground" />
              </div>
            </div>
            <div className="space-y-4">
              <div className="space-y-1">
                <p className="text-xs uppercase tracking-wide text-muted-foreground">Priority</p>
                <select
                  className="h-10 w-full max-w-sm rounded-md border border-input bg-background px-3 py-2 text-sm disabled:opacity-60"
                  value={selectedPriority}
                  disabled={!canEdit || saveMutation.isPending}
                  onChange={(event) => setSelectedPriority(event.target.value as Priority)}
                >
                  {["P1", "P2", "P3", "P4", "P5"].map((priority) => (
                    <option key={priority} value={priority}>
                      {priority}
                    </option>
                  ))}
                </select>
              </div>
              <div className="space-y-1">
                <p className="text-xs uppercase tracking-wide text-muted-foreground">Status</p>
                <select
                  className={`h-10 w-full max-w-sm rounded-md border bg-background px-3 py-2 text-sm disabled:opacity-60 ${
                    showResolutionError ? "border-red-500" : "border-input"
                  }`}
                  value={selectedStatus}
                  disabled={!canEdit || saveMutation.isPending}
                  onChange={(event) => setSelectedStatus(event.target.value as TicketStatus)}
                >
                  {["OPEN", "ASSIGNED", "WORK_IN_PROGRESS", "AWAITING_USER_INFO", "RESOLVED", "CLOSED"].map((status) => (
                    <option key={status} value={status}>
                      {formatEnumLabel(status)}
                    </option>
                  ))}
                </select>
              </div>
              <div className="space-y-1">
                <p className="text-xs uppercase tracking-wide text-muted-foreground">
                  Assignment group {requiresAssignment && <span className="text-red-400">*</span>}
                </p>
                <select
                  className={`h-10 w-full max-w-sm rounded-md border bg-background px-3 py-2 text-sm disabled:opacity-60 ${
                    showAssignmentGroupError ? "border-red-500" : "border-input"
                  }`}
                  value={selectedAssignmentGroup}
                  disabled={!canEdit || saveMutation.isPending}
                  onFocus={() => void groupsQuery.refetch()}
                  onChange={(event) => setSelectedAssignmentGroup(event.target.value)}
                >
                  <option value="">Unassigned</option>
                  {(groupsQuery.data ?? []).map((group) => (
                    <option key={group.id} value={group.id}>
                      {group.name}
                    </option>
                  ))}
                </select>
              </div>
              <div className="space-y-1">
                <p className="text-xs uppercase tracking-wide text-muted-foreground">
                  Assigned to {requiresAssignment && <span className="text-red-400">*</span>}
                </p>
                <select
                  value={selectedAssignee}
                  disabled={!canEdit || saveMutation.isPending}
                  onFocus={() => {
                    if (selectedAssignmentGroup && selectedGroupMemberIds.length > 0) {
                      void assigneesQuery.refetch()
                    }
                  }}
                  onChange={(event) => setSelectedAssignee(event.target.value)}
                  className={`h-10 w-full max-w-sm rounded-md border bg-background px-3 py-2 text-sm disabled:opacity-60 ${
                    showAssigneeError ? "border-red-500" : "border-input"
                  }`}
                >
                  <option value="">Unassigned</option>
                  {(assigneesQuery.data ?? []).map((member) => (
                    <option key={member.id} value={member.id}>
                      {member.userName} ({member.email})
                    </option>
                  ))}
                </select>
                {selectedAssignmentGroup && selectedGroupMemberIds.length === 0 && (
                  <p className="text-xs text-muted-foreground">No members found in this assignment group.</p>
                )}
              </div>
              {(showAssignmentGroupError || showAssigneeError) && (
                <p className="text-xs text-red-400">
                  {showAssignmentGroupError ? "* Assignment group is required for WORK_IN_PROGRESS. " : ""}
                  {showAssigneeError ? "* Assignee is required for WORK_IN_PROGRESS." : ""}
                </p>
              )}
              {(assignmentGroupChanged || assigneeChanged) && (
                <p className="text-xs text-muted-foreground">
                  Transition summary: status stays {formatEnumLabel(selectedStatus)}, assignment routing updated.
                </p>
              )}
              {showWipInternalCommentError && (
                <p className="text-xs text-red-400">
                  * Assignment/status updates require a WORK NOTE in the main comments section (set visibility to WORK NOTE).
                </p>
              )}
              {showPublicCommentError && (
                <p className="text-xs text-red-400">
                  * AWAITING_USER_INFO requires a PUBLIC comment in the main comments section.
                </p>
              )}
              {showResolutionError && (
                <p className="text-xs text-red-400">* Resolution note is required before resolving this ticket.</p>
              )}
            </div>
          </div>

          <div className="mx-auto w-full max-w-4xl space-y-6 border-t border-border pt-6">
            <div className="space-y-1">
              <p className="text-xs uppercase tracking-wide text-muted-foreground">Title</p>
              <Input
                value={draftTitle}
                disabled={!canEdit}
                onChange={(event) => setDraftTitle(event.target.value)}
                className="h-11 w-full"
              />
            </div>

            <div className="space-y-1">
              <p className="text-xs uppercase tracking-wide text-muted-foreground">Description</p>
              <Textarea
                value={draftDescription}
                disabled={!canEdit}
                onChange={(event) => setDraftDescription(event.target.value)}
                className="min-h-36 w-full"
              />
            </div>

          </div>
        </CardContent>
      </Card>

      {attachmentsOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4">
          <div className="w-full max-w-2xl rounded-lg border border-border bg-background p-5">
            <div className="mb-4 flex items-center justify-between">
              <h3 className="text-lg font-semibold">Attachments</h3>
              <Button variant="outline" size="sm" onClick={() => setAttachmentsOpen(false)}>
                Close
              </Button>
            </div>
            <div className="mb-4 flex items-center gap-2">
              <Input
                type="file"
                onChange={(event) => setAttachmentFile(event.target.files?.[0] ?? null)}
                className="h-10"
                disabled={!canUploadAttachment}
              />
              <Button
                type="button"
                onClick={() => uploadAttachmentMutation.mutate()}
                disabled={!canUploadAttachment || uploadAttachmentMutation.isPending || !attachmentFile}
              >
                {uploadAttachmentMutation.isPending ? "Uploading..." : "Upload"}
              </Button>
            </div>
            <div className="max-h-[60vh] space-y-2 overflow-y-auto">
              {attachmentsQuery.isLoading && <p className="text-sm text-muted-foreground">Loading attachments...</p>}
              {!attachmentsQuery.isLoading && (attachmentsQuery.data ?? []).length === 0 && (
                <p className="text-sm text-muted-foreground">No attachments uploaded for this ticket.</p>
              )}
              {(attachmentsQuery.data ?? []).map((attachment) => (
                <div key={attachment.id} className="rounded-md border border-border p-3 text-sm">
                  <p className="font-medium">{attachment.fileName}</p>
                  <p className="text-xs text-muted-foreground">
                    {attachment.uploadedByEmail} · {new Date(attachment.createdAt).toLocaleString()}
                  </p>
                  <p className="text-xs text-muted-foreground">
                    {attachment.contentType} · {Math.max(1, Math.round(attachment.fileSize / 1024))} KB
                  </p>
                  <Button
                    type="button"
                    variant="outline"
                    size="sm"
                    className="mt-2"
                    onClick={async () => {
                      const view = await caseManagerApi.tickets.getAttachmentViewUrl(ticketId, attachment.id)
                      window.open(view.url, "_blank", "noopener,noreferrer")
                    }}
                  >
                    Open attachment
                  </Button>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      <Card>
        <CardHeader>
          <CardTitle>Comments timeline</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {isStaff && (
            <div className="inline-flex items-center rounded-lg border border-border bg-muted/40 p-1">
              <Button
                type="button"
                variant={activityTab === "comments" ? "default" : "ghost"}
                size="sm"
                className="h-8 rounded-md px-3"
                onClick={() => setActivityTab("comments")}
              >
                Comments
              </Button>
              <Button
                type="button"
                variant={activityTab === "resolution" ? "default" : "ghost"}
                size="sm"
                className="h-8 rounded-md px-3"
                onClick={() => setActivityTab("resolution")}
              >
                Resolution
              </Button>
            </div>
          )}

          {isStaff ? (
            activityTab === "comments" ? (
              <>
                <div className="w-full space-y-2">
                  <div className="grid w-full gap-2 sm:grid-cols-[260px_1fr_auto]">
                    <div className="space-y-1">
                      <p className="text-xs uppercase tracking-wide text-muted-foreground">Visibility</p>
                      <div
                        className={`inline-flex w-full items-center rounded-lg border bg-background p-1 ${
                          (showWipInternalCommentError && visibility !== "INTERNAL") ||
                          (showPublicCommentError && visibility !== "PUBLIC")
                            ? "border-red-500"
                            : "border-input"
                        }`}
                      >
                        <Button
                          type="button"
                          size="sm"
                          variant={visibility === "PUBLIC" ? "default" : "ghost"}
                          className="h-8 flex-1 rounded-md"
                          onClick={() => setVisibility("PUBLIC")}
                          disabled={!canComment}
                        >
                          Public
                        </Button>
                        <Button
                          type="button"
                          size="sm"
                          variant={visibility === "INTERNAL" ? "default" : "ghost"}
                          className="h-8 flex-1 rounded-md"
                          onClick={() => setVisibility("INTERNAL")}
                          disabled={!canComment}
                        >
                          Work Note
                        </Button>
                      </div>
                    </div>
                    <Textarea
                      value={comment}
                      onChange={(event) => setComment(event.target.value)}
                      rows={2}
                      disabled={!canComment}
                      className={
                        (showWipInternalCommentError || showPublicCommentError) && !comment.trim()
                          ? "border-red-500 ring-1 ring-red-500/30"
                          : visibility === "INTERNAL"
                            ? "border-yellow-500/70 ring-1 ring-yellow-500/30"
                            : ""
                      }
                    />
                    <Button onClick={() => commentMutation.mutate()} disabled={!canComment || commentMutation.isPending || !comment}>
                      Add
                    </Button>
                  </div>
                  {!canComment && (
                    <p className="mx-auto w-full max-w-4xl text-xs text-muted-foreground">
                      Viewer mode is read-only. Commenting is disabled.
                    </p>
                  )}
                  {showWipInternalCommentError && (
                    <p className="mx-auto w-full max-w-4xl text-xs text-red-400">
                      * For assignment/status changes, set comment visibility to WORK NOTE and enter the internal comment here.
                    </p>
                  )}
                  {showPublicCommentError && (
                    <p className="mx-auto w-full max-w-4xl text-xs text-red-400">
                      * For AWAITING_USER_INFO, set visibility to PUBLIC and enter a requester-facing update.
                    </p>
                  )}
                  {[...ticket.comments]
                    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
                    .map((item) => (
                    <div
                      key={item.id}
                      className={`rounded-md border p-3 ${
                        item.visibility === "INTERNAL" ? "border-yellow-500/60 bg-yellow-500/5" : "border-border"
                      }`}
                    >
                      <div className="mb-1 flex items-center justify-between gap-2 text-xs text-muted-foreground">
                        <span className={item.visibility === "INTERNAL" ? "font-medium text-yellow-400" : ""}>
                          {item.visibility === "INTERNAL" ? "WORK NOTE" : item.visibility}
                        </span>{" "}
                        <span className="truncate">
                          {commentAuthorEmailById.get(item.authorId) ?? "unknown user"} ·{" "}
                          {new Date(item.createdAt).toLocaleString()}
                        </span>
                      </div>
                      <p className="text-sm">{item.message}</p>
                    </div>
                  ))}
                </div>
              </>
            ) : (
              <div className="mx-auto w-full max-w-4xl space-y-2">
                <p className="text-sm text-muted-foreground">
                  Add resolution details for the final fix. A resolution note is required before moving a ticket to
                  RESOLVED.
                </p>
                <Textarea
                  value={resolutionNote}
                  onChange={(event) => setResolutionNote(event.target.value)}
                  placeholder="Describe root cause, fix applied, and verification steps."
                  className={showResolutionError ? "border-red-500" : ""}
                  rows={5}
                />
                {showResolutionError && <p className="text-xs text-red-400">* Resolution note is required.</p>}
              </div>
            )
          ) : (
            <>
              <div className="w-full space-y-2">
                {!isViewer && (
                  <div className="grid w-full gap-2 sm:grid-cols-[1fr_auto]">
                    <Textarea
                      value={comment}
                      onChange={(event) => setComment(event.target.value)}
                      rows={2}
                      disabled={!canComment}
                      placeholder="Add a public update or follow-up..."
                    />
                    <Button onClick={() => commentMutation.mutate()} disabled={!canComment || commentMutation.isPending || !comment}>
                      Add
                    </Button>
                  </div>
                )}
                {isViewer && (
                  <p className="text-xs text-muted-foreground">
                    Viewer mode is read-only. Commenting is disabled.
                  </p>
                )}
                {[...ticket.comments]
                  .filter((item) => item.visibility === "PUBLIC")
                  .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
                  .map((item) => (
                  <div key={item.id} className="rounded-md border border-border p-3">
                    <div className="mb-1 flex items-center justify-between gap-2 text-xs text-muted-foreground">
                      <span>PUBLIC</span>
                      <span className="truncate">
                        {commentAuthorEmailById.get(item.authorId) ?? "unknown user"} ·{" "}
                        {new Date(item.createdAt).toLocaleString()}
                      </span>
                    </div>
                    <p className="text-sm">{item.message}</p>
                  </div>
                ))}
                {[...ticket.comments].filter((item) => item.visibility === "PUBLIC").length === 0 && (
                  <p className="text-sm text-muted-foreground">No public comments yet.</p>
                )}
              </div>
            </>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
