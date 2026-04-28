import { Bell } from "lucide-react"
import { PageHeader } from "@/components/page-header"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

export const NotificationsPage = () => {
  return (
    <div className="space-y-6">
      <PageHeader title="Notifications" description="System notifications and user alerts." />
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2"><Bell className="size-4" /> Notification Center</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="rounded-xl border border-dashed border-border p-6 text-sm text-muted-foreground">
            No notification API is available yet. This page is ready for backend notification data when the endpoint is added.
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
