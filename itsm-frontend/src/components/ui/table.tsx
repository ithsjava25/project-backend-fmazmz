import type { HTMLAttributes, TableHTMLAttributes, TdHTMLAttributes, ThHTMLAttributes } from "react"
import { cn } from "@/lib/utils"

export const Table = ({ className, ...props }: TableHTMLAttributes<HTMLTableElement>) => (
  <div className="w-full overflow-auto rounded-xl border border-border/60 bg-background/70">
    <table className={cn("w-full caption-bottom text-sm", className)} {...props} />
  </div>
)

export const TableHeader = ({ className, ...props }: HTMLAttributes<HTMLTableSectionElement>) => (
  <thead className={cn("[&_tr]:border-b", className)} {...props} />
)

export const TableBody = ({ className, ...props }: HTMLAttributes<HTMLTableSectionElement>) => (
  <tbody className={cn("[&_tr:last-child]:border-0", className)} {...props} />
)

export const TableRow = ({ className, ...props }: HTMLAttributes<HTMLTableRowElement>) => (
  <tr className={cn("border-b border-border/50 transition-colors hover:bg-accent/60", className)} {...props} />
)

export const TableHead = ({ className, ...props }: ThHTMLAttributes<HTMLTableCellElement>) => (
  <th className={cn("h-10 px-2 text-left align-middle font-medium text-muted-foreground", className)} {...props} />
)

export const TableCell = ({ className, ...props }: TdHTMLAttributes<HTMLTableCellElement>) => (
  <td className={cn("p-2 align-middle", className)} {...props} />
)
