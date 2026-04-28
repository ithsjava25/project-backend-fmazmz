import { useEffect, useRef, useState } from "react"
import { Link, NavLink, Outlet } from "react-router-dom"
import { Bell, ClipboardList, Gauge, LogOut, Search, Shield, UserCheck, Users, UserCircle, UsersRound, Workflow, FilePlus2 } from "lucide-react"
import { useQuery } from "@tanstack/react-query"
import { caseManagerApi } from "@/api/case-manager-client"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import { formatEnumLabel } from "@/lib/format"
import type { RoleName, UserResponse } from "@/types/api"

type AppShellProps = {
  user: UserResponse
  canUseRolePreview: boolean
  previewRole: RoleName | null
  onSetPreviewRole: (role: RoleName | null) => void
}

export const AppShell = ({ user, canUseRolePreview, previewRole, onSetPreviewRole }: AppShellProps) => {
  const [searchText, setSearchText] = useState("")
  const [debouncedSearchText, setDebouncedSearchText] = useState("")
  const [searchOpen, setSearchOpen] = useState(false)
  const searchContainerRef = useRef<HTMLDivElement | null>(null)
  const normalizedSearchText = debouncedSearchText.trim()
  const isAdmin = user.roles.includes("ADMIN")
  const isAgent = user.roles.includes("AGENT") || user.roles.includes("SUPER_AGENT")
  const isViewer = user.roles.includes("VIEWER")
  const isStaff = isAdmin || isAgent
  const showBackofficeSearch = isStaff
  const navSections = isStaff
    ? [
        {
          label: "Overview",
          items: [{ to: "/app/dashboard", label: "Dashboard", icon: Gauge }],
        },
        {
          label: "Incident",
          items: [
            { to: "/app/tickets", label: "All", icon: ClipboardList },
            { to: "/app/tickets/assigned-to-me", label: "Assigned To Me", icon: UserCheck },
            { to: "/app/tickets/assigned-to-my-groups", label: "Assigned To My Groups", icon: UsersRound },
          ],
        },
        ...(isAdmin
          ? [
              {
                label: "Administration",
                items: [
                  { to: "/app/assignment-groups", label: "Assignment Groups", icon: Workflow },
                  { to: "/app/users", label: "Users & Roles", icon: Users },
                  { to: "/app/audit", label: "Audit Logs", icon: Shield },
                ],
              },
            ]
          : []),
        {
          label: "Personal",
          items: [
            { to: "/app/notifications", label: "Notifications", icon: Bell },
            { to: "/app/settings", label: "Settings", icon: UserCircle },
          ],
        },
      ]
    : [
        {
          label: "Front Office",
          items: [
            { to: "/app/my-reported-tickets", label: "My Reported Incidents", icon: ClipboardList },
            ...(!isViewer ? [{ to: "/app/report-issue", label: "Report an Issue", icon: FilePlus2 }] : []),
          ],
        },
        {
          label: "Personal",
          items: [
            { to: "/app/notifications", label: "Notifications", icon: Bell },
            { to: "/app/settings", label: "Settings", icon: UserCircle },
          ],
        },
      ]

  useEffect(() => {
    const timeout = window.setTimeout(() => {
      setDebouncedSearchText(searchText)
    }, 250)
    return () => window.clearTimeout(timeout)
  }, [searchText])

  const ticketsQuery = useQuery({
    queryKey: ["search", "tickets", normalizedSearchText],
    queryFn: () => caseManagerApi.tickets.search(normalizedSearchText, { size: 6 }),
    enabled: normalizedSearchText.length > 0,
  })

  const groupsQuery = useQuery({
    queryKey: ["search", "groups", normalizedSearchText],
    queryFn: () => caseManagerApi.assignmentGroups.search(normalizedSearchText),
    enabled: showBackofficeSearch && normalizedSearchText.length > 0,
  })

  const usersQuery = useQuery({
    queryKey: ["search", "users", normalizedSearchText],
    queryFn: () => caseManagerApi.users.search(normalizedSearchText),
    enabled: showBackofficeSearch && normalizedSearchText.length > 0,
  })

  useEffect(() => {
    const onPointerDown = (event: MouseEvent) => {
      if (!searchContainerRef.current) {
        return
      }
      if (!searchContainerRef.current.contains(event.target as Node)) {
        setSearchOpen(false)
      }
    }
    window.addEventListener("mousedown", onPointerDown)
    return () => window.removeEventListener("mousedown", onPointerDown)
  }, [])

  const preview = {
    tickets: ticketsQuery.data?.items ?? [],
    groups: groupsQuery.data ?? [],
    users: usersQuery.data ?? [],
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="grid min-h-screen grid-cols-1 lg:grid-cols-[280px_1fr]">
        <aside className="hidden border-r border-border bg-card px-4 py-5 lg:block">
          <Link to="/app/dashboard" className="mb-7 block rounded-lg border border-border/80 bg-background/80 px-3 py-3">
            <p className="text-lg font-semibold tracking-tight">Case Manager</p>
            <p className="text-xs text-muted-foreground">IT service management</p>
          </Link>
          <nav className="space-y-4">
            {navSections.map((section, index) => (
              <div key={section.label}>
                {index > 0 && <div className="mb-3 border-t border-border/70" />}
                <div className="mb-2 px-3 text-[11px] font-semibold uppercase tracking-[0.18em] text-muted-foreground">
                  {section.label}
                </div>
                <div className="space-y-1">
                  {section.items.map(({ to, label, icon: Icon }) => (
                    <NavLink
                      key={to}
                      to={to}
                      className={({ isActive }) =>
                        `group flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition-all ${
                          isActive
                            ? "bg-primary text-primary-foreground"
                            : "text-muted-foreground hover:bg-muted hover:text-foreground"
                        }`
                      }
                    >
                      <Icon className="size-4 transition-transform group-hover:scale-105" />
                      {label}
                    </NavLink>
                  ))}
                </div>
              </div>
            ))}
          </nav>
          <div className="mt-8 rounded-2xl border border-border/60 bg-background/70 p-4">
            <p className="text-sm font-medium">Signed in</p>
            <p className="mt-1 break-all text-xs text-muted-foreground">{user.email}</p>
          </div>
        </aside>

        <div className="flex min-h-screen flex-col">
          <header className="sticky top-0 z-10 border-b border-border bg-background px-4 py-3 sm:px-6">
            <div className="flex items-center justify-between gap-4">
              <div ref={searchContainerRef} className="relative w-full max-w-xl">
                <Search className="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                  className="h-11 rounded-2xl bg-background pl-10"
                  placeholder={showBackofficeSearch ? "Search incidents, groups, and users..." : "Search incidents..."}
                  value={searchText}
                  onFocus={() => setSearchOpen(true)}
                  onChange={(event) => {
                    setSearchText(event.target.value)
                    setSearchOpen(true)
                  }}
                />
                {searchOpen && (
                  <div className="absolute left-0 right-0 top-[calc(100%+8px)] z-30 max-h-[70vh] overflow-auto rounded-xl border border-border bg-popover p-2 shadow-xl">
                    <div className="space-y-2">
                      <div>
                        <p className="px-2 py-1 text-[11px] font-semibold uppercase tracking-wide text-muted-foreground">Incidents</p>
                        {normalizedSearchText.length === 0 ? (
                          <p className="px-2 py-1 text-xs text-muted-foreground">Start typing to search incidents.</p>
                        ) : ticketsQuery.isLoading ? (
                          <p className="px-2 py-1 text-xs text-muted-foreground">Searching incidents...</p>
                        ) : null}
                        {preview.tickets.length > 0 ? (
                          preview.tickets.map((ticket) => (
                            <Link
                              key={ticket.id}
                              to={`/app/tickets/${ticket.id}`}
                              className="block rounded-lg px-2 py-2 hover:bg-muted"
                              onClick={() => setSearchOpen(false)}
                            >
                              <p className="text-sm font-medium">{ticket.number} · {ticket.title}</p>
                              <p className="text-xs text-muted-foreground">
                                {formatEnumLabel(ticket.status)} · {ticket.priority}
                              </p>
                            </Link>
                          ))
                        ) : normalizedSearchText.length > 0 ? (
                          <p className="px-2 py-1 text-xs text-muted-foreground">No matching incidents.</p>
                        ) : null}
                      </div>

                      {showBackofficeSearch && (
                        <div className="border-t border-border/70 pt-2">
                          <p className="px-2 py-1 text-[11px] font-semibold uppercase tracking-wide text-muted-foreground">Assignment Groups</p>
                          {normalizedSearchText.length === 0 ? (
                            <p className="px-2 py-1 text-xs text-muted-foreground">Start typing to search groups.</p>
                          ) : groupsQuery.isLoading ? (
                            <p className="px-2 py-1 text-xs text-muted-foreground">Searching groups...</p>
                          ) : null}
                          {preview.groups.length > 0 ? (
                            preview.groups.map((group) => (
                              <Link
                                key={group.id}
                                to={`/app/assignment-groups/${group.id}`}
                                className="block rounded-lg px-2 py-2 hover:bg-muted"
                                onClick={() => setSearchOpen(false)}
                              >
                                <p className="text-sm font-medium">{group.name}</p>
                                <p className="text-xs text-muted-foreground">{group.memberIds.length} members</p>
                              </Link>
                            ))
                          ) : normalizedSearchText.length > 0 ? (
                            <p className="px-2 py-1 text-xs text-muted-foreground">No matching groups.</p>
                          ) : null}
                        </div>
                      )}

                      {showBackofficeSearch && (
                        <div className="border-t border-border/70 pt-2">
                          <p className="px-2 py-1 text-[11px] font-semibold uppercase tracking-wide text-muted-foreground">Users</p>
                          {normalizedSearchText.length === 0 ? (
                            <p className="px-2 py-1 text-xs text-muted-foreground">Start typing to search users.</p>
                          ) : usersQuery.isLoading ? (
                            <p className="px-2 py-1 text-xs text-muted-foreground">Searching users...</p>
                          ) : null}
                          {preview.users.length > 0 ? (
                            preview.users.map((account) => (
                              <Link
                                key={account.id}
                                to={`/app/users/${account.id}`}
                                className="block rounded-lg px-2 py-2 hover:bg-muted"
                                onClick={() => setSearchOpen(false)}
                              >
                                <p className="text-sm font-medium">{account.userName || "(No username)"}</p>
                                <p className="text-xs text-muted-foreground">{account.email}</p>
                              </Link>
                            ))
                          ) : normalizedSearchText.length > 0 ? (
                            <p className="px-2 py-1 text-xs text-muted-foreground">No matching users.</p>
                          ) : null}
                        </div>
                      )}
                    </div>
                  </div>
                )}
              </div>
              <div className="flex items-center gap-3">
                {canUseRolePreview && (
                  <div className="hidden items-center gap-1 md:flex">
                    <Button variant={previewRole === null ? "default" : "outline"} size="sm" onClick={() => onSetPreviewRole(null)}>Real</Button>
                    <Button variant={previewRole === "ADMIN" ? "default" : "outline"} size="sm" onClick={() => onSetPreviewRole("ADMIN")}>Admin</Button>
                    <Button variant={previewRole === "AGENT" ? "default" : "outline"} size="sm" onClick={() => onSetPreviewRole("AGENT")}>Agent</Button>
                    <Button variant={previewRole === "SUPER_AGENT" ? "default" : "outline"} size="sm" onClick={() => onSetPreviewRole("SUPER_AGENT")}>Super Agent</Button>
                    <Button variant={previewRole === "REPORTER" ? "default" : "outline"} size="sm" onClick={() => onSetPreviewRole("REPORTER")}>Reporter</Button>
                    <Button variant={previewRole === "VIEWER" ? "default" : "outline"} size="sm" onClick={() => onSetPreviewRole("VIEWER")}>Viewer</Button>
                  </div>
                )}
                <div className="hidden text-right md:block">
                  <p className="text-sm font-medium">{user.userName}</p>
                  <p className="text-xs text-muted-foreground">{user.email}</p>
                </div>
                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <Button variant="outline" size="sm" className="rounded-xl bg-background">
                      Account
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end">
                    <DropdownMenuItem asChild>
                      <Link to="/app/settings">Profile & settings</Link>
                    </DropdownMenuItem>
                    <DropdownMenuItem asChild>
                      <Link to="/logout" className="flex items-center">
                        <LogOut className="mr-2 size-4" />
                        Sign out
                      </Link>
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              </div>
            </div>
          </header>

          <main className="flex-1 px-4 py-7 sm:px-8">
            <Outlet />
          </main>
        </div>
      </div>
    </div>
  )
}
