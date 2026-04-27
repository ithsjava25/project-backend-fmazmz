import { Badge } from "@/components/ui/badge"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"

type AuthTransitionScreenProps = {
  phase: "signing-in" | "welcome"
  userName?: string
}

export const AuthTransitionScreen = ({ phase, userName }: AuthTransitionScreenProps) => {
  const isWelcome = phase === "welcome"

  return (
    <main className="relative flex min-h-screen items-center justify-center overflow-hidden px-4 py-10">
      <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_top_right,hsl(var(--primary)/0.25),transparent_55%)]" />
      <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_bottom_left,hsl(var(--muted-foreground)/0.2),transparent_55%)]" />

      <div className="pointer-events-none absolute inset-0 opacity-45">
        <div className="absolute -left-20 top-20 size-64 animate-pulse rounded-full bg-primary/20 blur-3xl" />
        <div className="absolute -right-20 bottom-16 size-72 animate-pulse rounded-full bg-muted-foreground/20 blur-3xl [animation-delay:300ms]" />
      </div>

      <Card className="relative w-full max-w-lg border-border/70 bg-background/90 shadow-2xl backdrop-blur">
        <CardHeader className="space-y-4 text-center">
          <div className="flex justify-center">
            <Badge variant="outline">Case Manager</Badge>
          </div>
          <div className="space-y-2">
            <CardTitle className="text-2xl tracking-tight">
              {isWelcome ? `Welcome${userName ? `, ${userName}` : ""}` : "Signing you in"}
            </CardTitle>
            <CardDescription>
              {isWelcome
                ? "Your workspace is ready. Loading tickets and service desk tools..."
                : "Securely connecting your GitHub session..."}
            </CardDescription>
          </div>
        </CardHeader>
        <CardContent className="space-y-5">
          <div className="grid justify-items-center gap-3">
            <span className="relative mx-auto inline-flex size-10 items-center justify-center">
              <span className="absolute left-1/2 top-1/2 inline-flex size-10 -translate-x-1/2 -translate-y-1/2 animate-ping rounded-full bg-primary/20" />
              <span className="absolute left-1/2 top-1/2 inline-flex size-7 -translate-x-1/2 -translate-y-1/2 rounded-full border-2 border-primary/60" />
              <span className="inline-flex size-3 animate-pulse rounded-full bg-primary" />
            </span>
            <div className="flex items-center justify-center gap-1.5">
              <span className="size-1.5 animate-bounce rounded-full bg-primary [animation-delay:0ms]" />
              <span className="size-1.5 animate-bounce rounded-full bg-primary/80 [animation-delay:150ms]" />
              <span className="size-1.5 animate-bounce rounded-full bg-primary/60 [animation-delay:300ms]" />
            </div>
          </div>

          <div className="h-1.5 overflow-hidden rounded-full bg-muted/80">
            <div
              className={`h-full w-2/3 rounded-full bg-primary transition-all duration-700 ease-out ${
                isWelcome ? "translate-x-8" : "translate-x-1"
              }`}
            />
          </div>
          <div className="space-y-2">
            <div className="h-2 w-2/3 animate-pulse rounded bg-muted/70" />
            <div className="h-2 w-1/2 animate-pulse rounded bg-muted/55 [animation-delay:180ms]" />
          </div>
        </CardContent>
      </Card>
    </main>
  )
}
