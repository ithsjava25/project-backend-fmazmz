import type { HTMLAttributes } from "react"
import { cn } from "@/lib/utils"

type BadgeProps = HTMLAttributes<HTMLSpanElement> & {
  variant?: "default" | "outline"
}

export const Badge = ({ className, variant = "default", ...props }: BadgeProps) => (
  <span
    className={cn(
      "inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium",
      variant === "outline" ? "border border-border bg-background" : "bg-primary text-primary-foreground",
      className,
    )}
    {...props}
  />
)
