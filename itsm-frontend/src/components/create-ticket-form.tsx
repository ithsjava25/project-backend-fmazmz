import { useState, type FormEvent } from "react"
import { caseManagerApi } from "@/api/case-manager-client"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import type { CreateTicketRequest } from "@/types/api"

const initialState: CreateTicketRequest = {
  title: "",
  description: "",
  type: "INCIDENT",
}

export const CreateTicketForm = () => {
  const [form, setForm] = useState<CreateTicketRequest>(initialState)
  const [submitting, setSubmitting] = useState(false)
  const [result, setResult] = useState<string>("")

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setSubmitting(true)
    setResult("")
    try {
      const ticket = await caseManagerApi.tickets.create(form)
      setResult(`Created ticket ${ticket.number} (${ticket.id})`)
      setForm(initialState)
    } catch (error) {
      const message = error instanceof Error ? error.message : "Unexpected error"
      setResult(message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Create ticket</CardTitle>
        <CardDescription>Starter form wired to <code>POST /api/v1/tickets</code>.</CardDescription>
      </CardHeader>
      <CardContent>
        <form className="space-y-3" onSubmit={submit}>
          <Input
            value={form.title}
            onChange={(event) => setForm((current) => ({ ...current, title: event.target.value }))}
            placeholder="Title"
            required
          />
          <Textarea
            value={form.description}
            onChange={(event) =>
              setForm((current) => ({ ...current, description: event.target.value }))
            }
            placeholder="Description"
            rows={4}
            required
          />
          <div className="flex gap-2">
            <Button
              type="button"
              variant={form.type === "INCIDENT" ? "default" : "outline"}
              onClick={() => setForm((current) => ({ ...current, type: "INCIDENT" }))}
            >
              Incident
            </Button>
            <Button
              type="button"
              variant={form.type === "REQUEST" ? "default" : "outline"}
              onClick={() => setForm((current) => ({ ...current, type: "REQUEST" }))}
            >
              Request
            </Button>
          </div>
          <Button disabled={submitting} type="submit">
            {submitting ? "Creating..." : "Create ticket"}
          </Button>
          {result && <p className="text-sm text-muted-foreground">{result}</p>}
        </form>
      </CardContent>
    </Card>
  )
}
