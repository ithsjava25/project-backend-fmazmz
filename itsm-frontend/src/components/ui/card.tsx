import type { HTMLAttributes } from "react"
import { cn } from "@/lib/utils"

export const Card = ({ className, ...props }: HTMLAttributes<HTMLDivElement>) => (
  <div
    className={cn(
      "rounded-2xl border border-border/70 bg-card/90 text-card-foreground shadow-[0_8px_30px_rgb(0,0,0,0.04)] backdrop-blur-sm",
      className,
    )}
    {...props}
  />
)

export const CardHeader = ({ className, ...props }: HTMLAttributes<HTMLDivElement>) => (
  <div className={cn("space-y-1.5 p-5", className)} {...props} />
)

export const CardTitle = ({ className, ...props }: HTMLAttributes<HTMLHeadingElement>) => (
  <h3 className={cn("text-base font-semibold leading-none tracking-tight", className)} {...props} />
)

export const CardDescription = ({ className, ...props }: HTMLAttributes<HTMLParagraphElement>) => (
  <p className={cn("text-sm text-muted-foreground", className)} {...props} />
)

export const CardContent = ({ className, ...props }: HTMLAttributes<HTMLDivElement>) => (
  <div className={cn("p-5 pt-0", className)} {...props} />
)
