import { useEffect, useState } from "react"
import { buttonVariants } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { cn } from "@/lib/utils"
import { env } from "@/config/env"

export const OpenApiPanel = () => {
  const [openApiStatus, setOpenApiStatus] = useState("Checking OpenAPI endpoint...")

  useEffect(() => {
    const checkOpenApi = async () => {
      try {
        const response = await fetch(env.openapiJsonUrl, { credentials: "include" })
        if (!response.ok) {
          setOpenApiStatus(`OpenAPI endpoint returned ${response.status} ${response.statusText}.`)
          return
        }
        const payload = (await response.json()) as { openapi?: string; info?: { title?: string } }
        const version = payload.openapi ?? "unknown"
        const title = payload.info?.title ?? "Case Manager API"
        setOpenApiStatus(`Connected to ${title} (OpenAPI ${version}).`)
      } catch (error) {
        const message = error instanceof Error ? error.message : "Unknown error"
        setOpenApiStatus(`Could not reach OpenAPI JSON: ${message}`)
      }
    }

    void checkOpenApi()
  }, [])

  return (
    <Card>
      <CardHeader>
        <CardTitle>OpenAPI docs</CardTitle>
        <CardDescription>
          Use Swagger UI for exploration and <code>/v3/api-docs</code> for client generation.
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="flex flex-wrap gap-2">
          <a
            className={cn(buttonVariants())}
            href={env.openapiUrl}
            target="_blank"
            rel="noreferrer"
          >
            Open Swagger UI
          </a>
          <a
            className={cn(buttonVariants({ variant: "outline" }))}
            href={env.openapiJsonUrl}
            target="_blank"
            rel="noreferrer"
          >
            Open OpenAPI JSON
          </a>
        </div>
        <div className="rounded-md border border-border bg-muted/30 p-4 text-sm text-muted-foreground">
          <p>
            Some browsers block embedded localhost docs for security (X-Frame-Options / CSP). Open
            Swagger in a new tab using the button above.
          </p>
          <p className="mt-2">{openApiStatus}</p>
        </div>
      </CardContent>
    </Card>
  )
}
