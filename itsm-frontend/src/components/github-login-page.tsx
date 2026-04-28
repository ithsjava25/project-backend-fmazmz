import { buttonVariants } from "@/components/ui/button"
import { cn } from "@/lib/utils"

type GithubLoginPageProps = {
  loginUrl: string
  error?: string | null
}

const AUTH_REDIRECT_FLAG = "case-manager-auth-redirect-started"

export const GithubLoginPage = ({ loginUrl, error }: GithubLoginPageProps) => {
  return (
    <main className="flex min-h-screen flex-col items-center justify-center bg-black px-6 py-12">
      <div className="w-full max-w-[400px] space-y-10">
        <header className="text-center">
          <h1 className="text-lg font-semibold tracking-tight text-white">Case Manager</h1>
          <p className="mt-2 text-sm text-neutral-400">Sign in with GitHub to access the workspace.</p>
        </header>

        <div className="border border-neutral-800 bg-neutral-950 px-8 py-10">
          <a
            href={loginUrl}
            className={cn(
              buttonVariants({ size: "lg" }),
              "h-11 w-full rounded-md border-0 bg-white text-black shadow-none hover:bg-neutral-200",
            )}
            onClick={() => {
              localStorage.setItem(AUTH_REDIRECT_FLAG, "1")
            }}
          >
            <svg aria-hidden="true" viewBox="0 0 24 24" className="size-4 fill-current">
              <path d="M12 .5C5.648.5.5 5.648.5 12a11.5 11.5 0 0 0 7.863 10.915c.575.106.787-.25.787-.556 0-.274-.01-1-.016-1.962-3.2.696-3.876-1.542-3.876-1.542-.523-1.329-1.278-1.682-1.278-1.682-1.044-.714.079-.7.079-.7 1.154.081 1.76 1.184 1.76 1.184 1.025 1.756 2.689 1.249 3.344.955.104-.742.401-1.249.729-1.536-2.555-.291-5.242-1.278-5.242-5.688 0-1.257.45-2.285 1.183-3.091-.119-.292-.513-1.465.112-3.054 0 0 .966-.309 3.166 1.181a11.012 11.012 0 0 1 5.765 0c2.198-1.49 3.163-1.181 3.163-1.181.626 1.589.233 2.762.114 3.054.736.806 1.181 1.834 1.181 3.091 0 4.421-2.691 5.393-5.254 5.678.413.355.781 1.054.781 2.124 0 1.534-.014 2.771-.014 3.149 0 .309.208.668.794.555A11.502 11.502 0 0 0 23.5 12C23.5 5.648 18.352.5 12 .5Z" />
            </svg>
            Continue with GitHub
          </a>
          <p className="mt-6 text-center text-xs text-neutral-500">Session is established via GitHub OAuth on the application server.</p>
          {error && <p className="mt-4 text-center text-sm text-red-400">{error}</p>}
        </div>
      </div>
    </main>
  )
}
