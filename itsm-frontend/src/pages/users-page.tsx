import { useState } from "react"
import { useMutation } from "@tanstack/react-query"
import { toast } from "sonner"
import { caseManagerApi } from "@/api/case-manager-client"
import { PageHeader } from "@/components/page-header"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import type { RoleName } from "@/types/api"

export const UsersPage = () => {
  const [email, setEmail] = useState("")
  const [role, setRole] = useState<RoleName>("AGENT")
  const [rolesEmail, setRolesEmail] = useState("")
  const [rolesCsv, setRolesCsv] = useState("AGENT")

  const createMutation = useMutation({
    mutationFn: () => caseManagerApi.adminUsers.create({ email, role }),
    onSuccess: () => {
      toast.success("User created")
      setEmail("")
    },
    onError: (error: Error) => toast.error(error.message),
  })

  const replaceMutation = useMutation({
    mutationFn: () =>
      caseManagerApi.adminUsers.replaceRoles({
        email: rolesEmail,
        roles: rolesCsv.split(",").map((item) => item.trim()).filter(Boolean),
      }),
    onSuccess: () => toast.success("Roles replaced"),
    onError: (error: Error) => toast.error(error.message),
  })

  return (
    <div className="space-y-6">
      <PageHeader title="Users & Roles" description="Provision internal users and update role assignments." />
      <section className="grid gap-4 md:grid-cols-2">
        <Card>
          <CardHeader><CardTitle>Create user</CardTitle></CardHeader>
          <CardContent className="space-y-3">
            <Input placeholder="email@example.com" value={email} onChange={(event) => setEmail(event.target.value)} />
            <select
              className="h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
              value={role}
              onChange={(event) => setRole(event.target.value as RoleName)}
            >
              {["ADMIN", "AGENT", "REPORTER", "VIEWER"].map((item) => (
                <option key={item} value={item}>{item}</option>
              ))}
            </select>
            <Button disabled={!email || createMutation.isPending} onClick={() => createMutation.mutate()}>
              {createMutation.isPending ? "Creating..." : "Create user"}
            </Button>
          </CardContent>
        </Card>

        <Card>
          <CardHeader><CardTitle>Replace roles</CardTitle></CardHeader>
          <CardContent className="space-y-3">
            <Input
              placeholder="email@example.com"
              value={rolesEmail}
              onChange={(event) => setRolesEmail(event.target.value)}
            />
            <Input
              placeholder="CSV roles e.g. AGENT,REPORTER"
              value={rolesCsv}
              onChange={(event) => setRolesCsv(event.target.value)}
            />
            <Button disabled={!rolesEmail || replaceMutation.isPending} onClick={() => replaceMutation.mutate()}>
              {replaceMutation.isPending ? "Updating..." : "Replace roles"}
            </Button>
          </CardContent>
        </Card>
      </section>
    </div>
  )
}
