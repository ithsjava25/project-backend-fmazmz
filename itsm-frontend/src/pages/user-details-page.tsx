import { useEffect, useState } from "react"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { Link, useParams } from "react-router-dom"
import { toast } from "sonner"
import { caseManagerApi } from "@/api/case-manager-client"
import { PageHeader } from "@/components/page-header"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button, buttonVariants } from "@/components/ui/button"
import type { RoleName } from "@/types/api"

const ROLE_OPTIONS: RoleName[] = ["ADMIN", "AGENT", "SUPER_AGENT", "REPORTER", "VIEWER"]

export const UserDetailsPage = () => {
  const { userId = "" } = useParams()
  const queryClient = useQueryClient()
  const [selectedRoles, setSelectedRoles] = useState<RoleName[]>([])

  const userQuery = useQuery({
    queryKey: ["admin-user", userId],
    queryFn: () => caseManagerApi.adminUsers.getById(userId),
    enabled: Boolean(userId),
  })

  const account = userQuery.data

  useEffect(() => {
    if (!account) {
      return
    }
    setSelectedRoles(account.roles as RoleName[])
  }, [account])

  const replaceRolesMutation = useMutation({
    mutationFn: () =>
      caseManagerApi.adminUsers.replaceRoles({
        email: account!.email,
        roles: selectedRoles,
      }),
    onSuccess: () => {
      toast.success("Roles updated")
      void queryClient.invalidateQueries({ queryKey: ["admin-user", userId] })
      void queryClient.invalidateQueries({ queryKey: ["admin-users"] })
    },
    onError: (error: Error) => toast.error(error.message),
  })

  const toggleRole = (roleName: RoleName) => {
    setSelectedRoles((current) =>
      current.includes(roleName)
        ? current.filter((item) => item !== roleName)
        : [...current, roleName],
    )
  }

  if (!account) {
    return <p className="text-sm text-muted-foreground">Loading user account...</p>
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title={account.userName || account.email}
        description="Review user profile and replace role assignments."
      />
      <div>
        <Link to="/app/users" className={buttonVariants({ variant: "outline", size: "sm" })}>
          Back to users
        </Link>
      </div>
      <section className="grid gap-4 xl:grid-cols-[1fr_420px]">
        <Card>
          <CardHeader className="border-b border-border">
            <CardTitle>Account details</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3 pt-5 text-sm">
            <div className="rounded-md border border-border p-3">
              <p className="text-xs uppercase tracking-wide text-muted-foreground">Name</p>
              <p className="font-medium">{account.userName || "-"}</p>
            </div>
            <div className="rounded-md border border-border p-3">
              <p className="text-xs uppercase tracking-wide text-muted-foreground">Email</p>
              <p className="font-medium">{account.email}</p>
            </div>
            <div className="rounded-md border border-border p-3">
              <p className="text-xs uppercase tracking-wide text-muted-foreground">Provider</p>
              <p className="font-medium">{account.provider}</p>
            </div>
            <div className="rounded-md border border-border p-3">
              <p className="text-xs uppercase tracking-wide text-muted-foreground">Current roles</p>
              <p className="font-medium">{account.roles.join(", ") || "-"}</p>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="border-b border-border">
            <CardTitle>Replace roles</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4 pt-5">
            <div className="space-y-2">
              <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">Role set</p>
              <div className="flex flex-wrap gap-2">
                {ROLE_OPTIONS.map((item) => (
                  <Button
                    key={item}
                    type="button"
                    size="sm"
                    variant={selectedRoles.includes(item) ? "default" : "outline"}
                    onClick={() => toggleRole(item)}
                  >
                    {item}
                  </Button>
                ))}
              </div>
            </div>
            <p className="text-xs text-muted-foreground">
              Saving will replace all existing roles for this user.
            </p>
            <Button
              className="w-full"
              disabled={replaceRolesMutation.isPending || selectedRoles.length === 0}
              onClick={() => replaceRolesMutation.mutate()}
            >
              {replaceRolesMutation.isPending ? "Updating..." : "Save roles"}
            </Button>
          </CardContent>
        </Card>
      </section>
    </div>
  )
}

