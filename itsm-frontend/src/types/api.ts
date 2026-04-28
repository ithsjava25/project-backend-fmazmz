export type UUID = string
export type ISODate = string

export interface ApiResponseWrapper<T> {
  data: T
  requestId: UUID
  timestamp: number
}

export interface PageMetadata {
  page: number
  size: number
  offset: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
  hasNext: boolean
  hasPrevious: boolean
}

export interface PagedResult<T> {
  items: T[]
  page: PageMetadata
}

export type TicketStatus =
  | "OPEN"
  | "ASSIGNED"
  | "WORK_IN_PROGRESS"
  | "AWAITING_USER_INFO"
  | "RESOLVED"
  | "CLOSED"

export type Priority = "P5" | "P4" | "P3" | "P2" | "P1"
export type TicketType = "INCIDENT" | "REQUEST"
export type CommentVisibility = "PUBLIC" | "INTERNAL"
export type TicketAction =
  | "CREATE"
  | "READ"
  | "UPDATE"
  | "RESOLVE"
  | "ASSIGN"
  | "CHANGE_STATUS"
  | "CHANGE_PRIORITY"
  | "COMMENT_PUBLIC"
  | "COMMENT_INTERNAL"
  | "UPLOAD_ATTACHMENT"
  | "REOPEN"
  | "LOG_READ"
  | "MANAGE_ASSIGNMENT_GROUPS"

export type RoleName = "ADMIN" | "AGENT" | "REPORTER" | "VIEWER"

export interface TicketCommentResponse {
  id: UUID
  visibility: CommentVisibility
  ticketId: UUID
  authorId: UUID
  message: string
  createdAt: ISODate
}

export interface TicketResponse {
  id: UUID
  number: string
  type: TicketType
  title: string
  description: string
  comments: TicketCommentResponse[]
  resolutionNotes: string | null
  requesterId: UUID
  assigneeId: UUID | null
  assignmentGroupId: UUID | null
  assignmentGroupName: string | null
  status: TicketStatus
  priority: Priority
  createdAt: ISODate
  updatedAt: ISODate
}

export interface CreateTicketRequest {
  title: string
  description: string
  type: TicketType
  assignmentGroupId?: UUID
}

export interface ChangeTicketStatusRequest {
  status: TicketStatus
  assignmentGroup?: UUID
  assignee?: UUID
  publicComment?: string
  internalComment?: string
  resolutionNotes?: string
}

export interface UpdateTicketPriorityRequest {
  priority: Priority
  internalComment: string
}

export interface UpdateTicketRequest {
  title?: string
  description?: string
}

export interface TicketCommentRequest {
  visibility: CommentVisibility
  comment: string
}

export interface AttachmentViewUrlResponse {
  url: string
  expiresAt: ISODate
}

export interface AttachmentSummaryResponse {
  id: UUID
  fileName: string
  contentType: string
  fileSize: number
  uploadedByEmail: string
  createdAt: ISODate
}

export interface AssignmentGroupResponse {
  id: UUID
  name: string
  description: string | null
  memberIds: UUID[]
  createdAt: ISODate
}

export interface CreateAssignmentGroupRequest {
  name: string
  description?: string
}

export interface UpdateAssignmentGroupRequest {
  name?: string
  description?: string
}

export interface ModifyGroupMembersRequest {
  userIds: UUID[]
}

export interface UserResponse {
  registered: boolean
  id: UUID
  provider: string
  userName: string
  email: string
  avatar: string
  roles: string[]
}

export interface CreateUserRequest {
  email: string
  role: RoleName
}

export interface UpdateUserRolesRequest {
  email: string
  roles: string[]
}

export interface AuditLogEntry {
  id: UUID
  ticketId: UUID
  actorEmail: string
  ticketAction: TicketAction
  field: string | null
  oldValue: string | null
  newValue: string | null
  createdAt: ISODate
}
