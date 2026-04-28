import React, { createContext, useContext, useState, type ReactElement, type ReactNode } from "react"

type DropdownContextValue = {
  open: boolean
  setOpen: (open: boolean) => void
}

const DropdownContext = createContext<DropdownContextValue | null>(null)

export const DropdownMenu = ({ children }: { children: ReactNode }) => {
  const [open, setOpen] = useState(false)
  return <div className="relative"><DropdownContext.Provider value={{ open, setOpen }}>{children}</DropdownContext.Provider></div>
}

export const DropdownMenuTrigger = ({ asChild, children }: { asChild?: boolean; children: ReactNode }) => {
  const context = useContext(DropdownContext)
  if (!context) return null

  if (asChild && React.isValidElement(children)) {
    const child = children as ReactElement<{ onClick?: () => void }>
    return React.cloneElement(child, {
      onClick: () => context.setOpen(!context.open),
    })
  }
  return <button onClick={() => context.setOpen(!context.open)}>{children}</button>
}

export const DropdownMenuContent = ({ children, align = "start" }: { children: ReactNode; align?: "start" | "end" }) => {
  const context = useContext(DropdownContext)
  if (!context?.open) return null
  return (
    <div className={`absolute z-20 mt-2 min-w-[180px] rounded-md border bg-background p-1 shadow ${align === "end" ? "right-0" : "left-0"}`}>
      {children}
    </div>
  )
}

export const DropdownMenuItem = ({ children }: { children: ReactNode; asChild?: boolean }) => (
  <div className="rounded-sm px-2 py-1.5 text-sm hover:bg-muted">{children}</div>
)
