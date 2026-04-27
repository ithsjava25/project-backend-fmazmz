import { buttonVariants } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { cn } from "@/lib/utils"

type GithubLoginPageProps = {
  loginUrl: string
  error?: string | null
}

const AUTH_REDIRECT_FLAG = "case-manager-auth-redirect-started"

export const GithubLoginPage = ({
  loginUrl,
  error,
}: GithubLoginPageProps) => {
  return (
    <main className="relative flex min-h-screen items-center justify-center overflow-hidden px-4 py-10">
      <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_top_right,hsl(var(--primary)/0.25),transparent_55%)]" />
      <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_bottom_left,hsl(var(--muted-foreground)/0.2),transparent_55%)]" />

      <Card className="relative w-full max-w-md border-border/70 bg-background/95 shadow-xl backdrop-blur">
        <CardHeader className="space-y-4">
          <Badge variant="outline" className="w-fit">
            Case Manager
          </Badge>
          <div className="space-y-1">
            <CardTitle className="text-2xl tracking-tight">Sign in with GitHub</CardTitle>
            <CardDescription>
              Continue with your GitHub account to access tickets, groups, and audit tools.
            </CardDescription>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          <a
            href={loginUrl}
            className={cn(buttonVariants({ size: "lg" }), "w-full")}
            onClick={() => {
              localStorage.setItem(AUTH_REDIRECT_FLAG, "1")
            }}
          >
            <svg
              aria-hidden="true"
              viewBox="0 0 24 24"
              className="size-4 fill-current"
            >
              <path d="M12 .5C5.648.5.5 5.648.5 12a11.5 11.5 0 0 0 7.863 10.915c.575.106.787-.25.787-.556 0-.274-.01-1-.016-1.962-3.2.696-3.876-1.542-3.876-1.542-.523-1.329-1.278-1.682-1.278-1.682-1.044-.714.079-.7.079-.7 1.154.081 1.76 1.184 1.76 1.184 1.025 1.756 2.689 1.249 3.344.955.104-.742.401-1.249.729-1.536-2.555-.291-5.242-1.278-5.242-5.688 0-1.257.45-2.285 1.183-3.091-.119-.292-.513-1.465.112-3.054 0 0 .966-.309 3.166 1.181a11.012 11.012 0 0 1 5.765 0c2.198-1.49 3.163-1.181 3.163-1.181.626 1.589.233 2.762.114 3.054.736.806 1.181 1.834 1.181 3.091 0 4.421-2.691 5.393-5.254 5.678.413.355.781 1.054.781 2.124 0 1.534-.014 2.771-.014 3.149 0 .309.208.668.794.555A11.502 11.502 0 0 0 23.5 12C23.5 5.648 18.352.5 12 .5Z" />
            </svg>
            Continue with GitHub
          </a>
          <p className="text-xs text-muted-foreground">
            Your session is managed by the backend OAuth2 flow and returned to this frontend.
          </p>
          {error && <p className="text-sm text-destructive">{error}</p>}
        </CardContent>
      </Card>
    </main>
  )
}
