import { useState, type FormEvent } from "react"
import { caseManagerApi } from "@/api/case-manager-client"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import type { CreateAssignmentGroupRequest } from "@/types/api"

const initialState: CreateAssignmentGroupRequest = {
  name: "",
  description: "",
}

export const CreateAssignmentGroupForm = () => {
  const [form, setForm] = useState<CreateAssignmentGroupRequest>(initialState)
  const [submitting, setSubmitting] = useState(false)
  const [result, setResult] = useState("")

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setSubmitting(true)
    setResult("")
    try {
      const group = await caseManagerApi.assignmentGroups.create(form)
      setResult(`Created group ${group.name} (${group.id})`)
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
        <CardTitle>Create assignment group</CardTitle>
        <CardDescription>
          Starter form wired to <code>POST /api/v1/assignment-groups</code>.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form className="space-y-3" onSubmit={submit}>
          <Input
            value={form.name}
            onChange={(event) => setForm((current) => ({ ...current, name: event.target.value }))}
            placeholder="Group name (e.g. L2 Support)"
            required
          />
          <Textarea
            value={form.description}
            onChange={(event) =>
              setForm((current) => ({ ...current, description: event.target.value }))
            }
            placeholder="Description"
            rows={4}
          />
          <Button disabled={submitting} type="submit">
            {submitting ? "Creating..." : "Create assignment group"}
          </Button>
          {result && <p className="text-sm text-muted-foreground">{result}</p>}
        </form>
      </CardContent>
    </Card>
  )
}
