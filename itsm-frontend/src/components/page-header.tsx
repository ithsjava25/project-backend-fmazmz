type PageHeaderProps = {
  title: string
  description: string
}

export const PageHeader = ({ title, description }: PageHeaderProps) => (
  <div className="mb-6">
    <h1 className="text-2xl font-semibold tracking-tight">{title}</h1>
    <p className="mt-1 max-w-3xl text-sm leading-6 text-muted-foreground">{description}</p>
  </div>
)
