import { Link, NavLink, Outlet } from "react-router-dom"
import { Bell, ClipboardList, Gauge, LogOut, Search, Shield, Users, UserCircle, Workflow } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import type { UserResponse } from "@/types/api"

type AppShellProps = {
  user: UserResponse
}

const navItems = [
  { to: "/app/dashboard", label: "Dashboard", icon: Gauge },
  { to: "/app/tickets", label: "Tickets", icon: ClipboardList },
  { to: "/app/assignment-groups", label: "Assignment Groups", icon: Workflow },
  { to: "/app/users", label: "Users & Roles", icon: Users },
  { to: "/app/audit", label: "Audit Logs", icon: Shield },
  { to: "/app/notifications", label: "Notifications", icon: Bell },
  { to: "/app/settings", label: "Settings", icon: UserCircle },
]

export const AppShell = ({ user }: AppShellProps) => {
  return (
    <div className="min-h-screen bg-background">
      <div className="grid min-h-screen grid-cols-1 lg:grid-cols-[280px_1fr]">
        <aside className="hidden border-r border-border bg-card px-4 py-5 lg:block">
          <Link to="/app/dashboard" className="mb-7 block rounded-lg border border-border/80 bg-background/80 px-3 py-3">
            <p className="text-lg font-semibold tracking-tight">Case Manager</p>
            <p className="text-xs text-muted-foreground">IT service management</p>
          </Link>
          <div className="mb-2 px-3 text-[11px] font-semibold uppercase tracking-[0.18em] text-muted-foreground">
            Workspace
          </div>
          <nav className="space-y-1">
            {navItems.map(({ to, label, icon: Icon }) => (
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
          </nav>
          <div className="mt-8 rounded-2xl border border-border/60 bg-background/70 p-4">
            <p className="text-sm font-medium">Signed in</p>
            <p className="mt-1 break-all text-xs text-muted-foreground">{user.email}</p>
          </div>
        </aside>

        <div className="flex min-h-screen flex-col">
          <header className="sticky top-0 z-10 border-b border-border bg-background px-4 py-3 sm:px-6">
            <div className="flex items-center justify-between gap-4">
              <div className="relative w-full max-w-xl">
                <Search className="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
                <Input className="h-11 rounded-2xl bg-background pl-10" placeholder="Search tickets, people, groups, audit trails..." />
              </div>
              <div className="flex items-center gap-3">
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
                      <Link to="/logout">
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
