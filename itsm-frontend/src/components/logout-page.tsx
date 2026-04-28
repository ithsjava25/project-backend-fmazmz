import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { env } from "@/config/env"

type LogoutPageProps = {
  onCancel: () => void
  onLoggedOut: () => void
  onError: (message: string) => void
}

export const LogoutPage = ({ onCancel, onLoggedOut, onError }: LogoutPageProps) => {
  const [loading, setLoading] = useState(false)

  const logout = () => {
    setLoading(true)
    // Use full-page navigation for Spring Security logout so redirects + cookies are handled by browser.
    // This avoids fetch/XHR edge-cases around auth redirects.
    try {
      const form = document.createElement("form")
      form.method = "POST"
      form.action = `${env.apiBaseUrl}/logout`
      form.style.display = "none"
      document.body.appendChild(form)
      form.submit()
      onLoggedOut()
    } catch (error) {
      const message = error instanceof Error ? error.message : "Unknown error"
      setLoading(false)
      onError(`Logout failed: ${message}`)
    }
  }

  return (
    <main className="flex min-h-screen items-center justify-center px-4">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle>Sign out</CardTitle>
          <CardDescription>
            This will end your current session and return you to the login page.
          </CardDescription>
        </CardHeader>
        <CardContent className="flex gap-2">
          <Button variant="outline" onClick={onCancel} disabled={loading}>
            Cancel
          </Button>
          <Button onClick={logout} disabled={loading}>
            {loading ? "Signing out..." : "Sign out"}
          </Button>
        </CardContent>
      </Card>
    </main>
  )
}
