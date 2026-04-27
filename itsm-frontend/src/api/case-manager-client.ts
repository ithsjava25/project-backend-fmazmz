import { request } from "@/api/http-client"
import type {
  AssignmentGroupResponse,
  AttachmentViewUrlResponse,
  AuditLogEntry,
  ChangeTicketStatusRequest,
  CreateAssignmentGroupRequest,
  CreateTicketRequest,
  CreateUserRequest,
  ModifyGroupMembersRequest,
  PagedResult,
  TicketCommentRequest,
  TicketResponse,
  UpdateAssignmentGroupRequest,
  UpdateTicketPriorityRequest,
  UpdateTicketRequest,
  UpdateUserRolesRequest,
  UserResponse,
  UUID,
} from "@/types/api"

type PaginationQuery = {
  page?: number
  size?: number
  sort?: string
}

export const caseManagerApi = {
  auth: {
    me: () => request<UserResponse>("/api/v1/auth/me"),
  },
  tickets: {
    list: (query: PaginationQuery = {}) =>
      request<PagedResult<TicketResponse>>("/api/v1/tickets", { query }),
    listByRequester: (userId: UUID, query: PaginationQuery = {}) =>
      request<PagedResult<TicketResponse>>(`/api/v1/tickets/requester/${userId}`, { query }),
    listByAssignee: (userId: UUID, query: PaginationQuery = {}) =>
      request<PagedResult<TicketResponse>>(`/api/v1/tickets/assignee/${userId}`, { query }),
    listByAssignmentGroup: (assignmentGroupId: UUID, query: PaginationQuery = {}) =>
      request<PagedResult<TicketResponse>>(`/api/v1/tickets/assignment-group/${assignmentGroupId}`, { query }),
    getById: (ticketId: UUID) => request<TicketResponse>(`/api/v1/tickets/${ticketId}`),
    getByNumber: (ticketNumber: string) => request<TicketResponse>(`/api/v1/tickets/number/${ticketNumber}`),
    create: (body: CreateTicketRequest) =>
      request<TicketResponse>("/api/v1/tickets", { method: "POST", body }),
    changeStatus: (ticketId: UUID, body: ChangeTicketStatusRequest) =>
      request<TicketResponse>(`/api/v1/tickets/${ticketId}/status`, { method: "PATCH", body }),
    changePriority: (ticketId: UUID, body: UpdateTicketPriorityRequest) =>
      request<TicketResponse>(`/api/v1/tickets/${ticketId}/priority`, { method: "PATCH", body }),
    update: (ticketId: UUID, body: UpdateTicketRequest) =>
      request<TicketResponse>(`/api/v1/tickets/${ticketId}`, { method: "PATCH", body }),
    comment: (ticketId: UUID, body: TicketCommentRequest) =>
      request<TicketResponse>(`/api/v1/tickets/${ticketId}/comment`, { method: "POST", body }),
    getAttachmentViewUrl: (ticketId: UUID, attachmentId: UUID) =>
      request<AttachmentViewUrlResponse>(`/api/v1/tickets/${ticketId}/attachments/${attachmentId}/view-url`),
  },
  assignmentGroups: {
    list: () => request<AssignmentGroupResponse[]>("/api/v1/assignment-groups"),
    getById: (groupId: UUID) => request<AssignmentGroupResponse>(`/api/v1/assignment-groups/${groupId}`),
    create: (body: CreateAssignmentGroupRequest) =>
      request<AssignmentGroupResponse>("/api/v1/assignment-groups", { method: "POST", body }),
    update: (groupId: UUID, body: UpdateAssignmentGroupRequest) =>
      request<AssignmentGroupResponse>(`/api/v1/assignment-groups/${groupId}`, { method: "PATCH", body }),
    addMembers: (groupId: UUID, body: ModifyGroupMembersRequest) =>
      request<AssignmentGroupResponse>(`/api/v1/assignment-groups/${groupId}/members`, { method: "POST", body }),
    removeMember: (groupId: UUID, userId: UUID) =>
      request<AssignmentGroupResponse>(`/api/v1/assignment-groups/${groupId}/members/${userId}`, {
        method: "DELETE",
      }),
  },
  audit: {
    list: (query: PaginationQuery = {}) => request<PagedResult<AuditLogEntry>>("/api/v1/audit", { query }),
    byTicket: (ticketId: UUID, query: PaginationQuery = {}) =>
      request<PagedResult<AuditLogEntry>>(`/api/v1/audit/${ticketId}`, { query }),
  },
  adminUsers: {
    create: (body: CreateUserRequest) =>
      request<UserResponse>("/api/v1/admin/users", { method: "POST", body }),
    replaceRoles: (body: UpdateUserRolesRequest) =>
      request<void>("/api/v1/admin/users/roles", { method: "PUT", body }),
  },
}
