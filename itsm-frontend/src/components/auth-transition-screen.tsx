import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"

type AuthTransitionScreenProps = {
  phase: "signing-in" | "welcome"
  userName?: string
}

export const AuthTransitionScreen = ({ phase, userName }: AuthTransitionScreenProps) => {
  const isWelcome = phase === "welcome"

  return (
    <main className="flex min-h-screen items-center justify-center bg-black px-4 py-10">
      <Card className="w-full max-w-lg border-neutral-800 bg-neutral-950 shadow-none">
        <CardHeader className="space-y-4 text-center">
          <p className="text-xs font-medium uppercase tracking-wider text-neutral-500">Case Manager</p>
          <div className="space-y-2">
            <CardTitle className="text-xl tracking-tight text-white">
              {isWelcome ? `Welcome${userName ? `, ${userName}` : ""}` : "Signing you in"}
            </CardTitle>
            <CardDescription className="text-neutral-400">
              {isWelcome
                ? "Your workspace is ready."
                : "Connecting your GitHub session…"}
            </CardDescription>
          </div>
        </CardHeader>
        <CardContent className="space-y-6 pb-8">
          <div className="flex justify-center">
            <span
              className="size-5 animate-spin rounded-full border-2 border-neutral-600 border-t-white"
              aria-hidden
            />
          </div>
          <div className="h-1 overflow-hidden rounded-full bg-neutral-800">
            <div
              className={`h-full rounded-full bg-neutral-200 transition-all duration-700 ease-out ${
                isWelcome ? "w-3/4" : "w-1/3"
              }`}
            />
          </div>
        </CardContent>
      </Card>
    </main>
  )
}
