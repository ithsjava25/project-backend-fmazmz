import { Link } from "react-router-dom"
import { buttonVariants } from "@/components/ui/button"
import { cn } from "@/lib/utils"

export const NotFoundPage = () => (
  <main className="flex min-h-screen flex-col items-center justify-center gap-4">
    <h1 className="text-2xl font-semibold">Page not found</h1>
    <p className="text-sm text-muted-foreground">The requested workspace route does not exist.</p>
    <Link to="/app/dashboard" className={cn(buttonVariants())}>
      Go to dashboard
    </Link>
  </main>
)
