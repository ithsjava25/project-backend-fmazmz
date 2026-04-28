import { Component, type ErrorInfo, type ReactNode } from "react"
import { Link } from "react-router-dom"
import { buttonVariants } from "@/components/ui/button"
import { cn } from "@/lib/utils"

type AppErrorBoundaryProps = {
  children: ReactNode
}

type AppErrorBoundaryState = {
  hasError: boolean
}

export class AppErrorBoundary extends Component<AppErrorBoundaryProps, AppErrorBoundaryState> {
  state: AppErrorBoundaryState = {
    hasError: false,
  }

  static getDerivedStateFromError(): AppErrorBoundaryState {
    return { hasError: true }
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error("Unhandled UI error", error, errorInfo)
  }

  render() {
    if (this.state.hasError) {
      return (
        <main className="flex min-h-screen flex-col items-center justify-center gap-3 px-4">
          <h1 className="text-2xl font-semibold">Something went wrong</h1>
          <p className="max-w-lg text-center text-sm text-muted-foreground">
            The workspace encountered an unexpected UI error. Reload or return to dashboard.
          </p>
          <Link to="/app/dashboard" className={cn(buttonVariants())}>
            Back to dashboard
          </Link>
        </main>
      )
    }
    return this.props.children
  }
}
