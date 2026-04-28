import { useState } from "react"
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { useNavigate } from "react-router-dom"
import { toast } from "sonner"
import { caseManagerApi } from "@/api/case-manager-client"
import { PageHeader } from "@/components/page-header"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import type { RoleName } from "@/types/api"

const ROLE_OPTIONS: RoleName[] = ["ADMIN", "AGENT", "REPORTER", "VIEWER"]

export const UserCreatePage = () => {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [email, setEmail] = useState("")
  const [role, setRole] = useState<RoleName>("AGENT")

  const createMutation = useMutation({
    mutationFn: () => caseManagerApi.adminUsers.create({ email, role }),
    onSuccess: (created) => {
      toast.success("User created")
      void queryClient.invalidateQueries({ queryKey: ["admin-users"] })
      navigate(`/app/users/${created.id}`)
    },
    onError: (error: Error) => toast.error(error.message),
  })

  return (
    <div className="space-y-6">
      <PageHeader
        title="Create User"
        description="Provision a user account with an initial role."
      />
      <Card className="mx-auto w-full max-w-2xl">
        <CardHeader className="border-b border-border">
          <CardTitle>User details</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4 pt-5">
          <div className="space-y-1">
            <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">Email</p>
            <Input
              placeholder="email@example.com"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
            />
          </div>
          <div className="space-y-2">
            <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">Initial role</p>
            <div className="flex flex-wrap gap-2">
              {ROLE_OPTIONS.map((item) => (
                <Button
                  key={item}
                  type="button"
                  size="sm"
                  variant={role === item ? "default" : "outline"}
                  onClick={() => setRole(item)}
                >
                  {item}
                </Button>
              ))}
            </div>
          </div>
          <div className="flex justify-end gap-2 border-t border-border pt-4">
            <Button variant="outline" onClick={() => navigate("/app/users")}>
              Cancel
            </Button>
            <Button
              disabled={createMutation.isPending || !email.trim()}
              onClick={() => createMutation.mutate()}
            >
              {createMutation.isPending ? "Creating..." : "Create user"}
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

