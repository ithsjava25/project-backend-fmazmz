import { useAuth } from "@/lib/auth-context"
import { PageHeader } from "@/components/page-header"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"

export const SettingsPage = () => {
  const { user } = useAuth()

  return (
    <div className="space-y-6">
      <PageHeader title="Profile" description="Authenticated user details from the current session." />
      <Card>
        <CardHeader><CardTitle>Authenticated User</CardTitle></CardHeader>
        <CardContent className="space-y-2">
          <p><span className="font-medium">Name:</span> {user.userName}</p>
          <p><span className="font-medium">Email:</span> {user.email}</p>
          <p><span className="font-medium">Provider:</span> {user.provider}</p>
          <div className="flex flex-wrap gap-2 pt-2">
            {user.roles.map((role) => (
              <Badge key={role} variant="outline">{role}</Badge>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
