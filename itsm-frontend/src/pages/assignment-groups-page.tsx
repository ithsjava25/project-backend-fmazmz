import { useQuery } from "@tanstack/react-query"
import { Link } from "react-router-dom"
import { caseManagerApi } from "@/api/case-manager-client"
import { PageHeader } from "@/components/page-header"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { buttonVariants } from "@/components/ui/button"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"

export const AssignmentGroupsPage = () => {
  const groupsQuery = useQuery({
    queryKey: ["assignment-groups"],
    queryFn: caseManagerApi.assignmentGroups.list,
  })
  const groups = groupsQuery.data ?? []

  return (
    <div className="space-y-6">
      <PageHeader
        title="Assignment Groups"
        description="Browse all assignment groups and open a group to manage details and membership."
      />
      <Card>
        <CardHeader className="flex flex-row items-center justify-between border-b border-border">
          <CardTitle>All groups</CardTitle>
          <Link to="/app/assignment-groups/new" className={buttonVariants()}>
            Create new group
          </Link>
        </CardHeader>
        <CardContent className="pt-5">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Name</TableHead>
                <TableHead>Description</TableHead>
                <TableHead>Members</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {groups.map((group) => (
                <TableRow key={group.id}>
                  <TableCell className="font-medium">
                    <Link to={`/app/assignment-groups/${group.id}`} className="hover:underline">
                      {group.name}
                    </Link>
                  </TableCell>
                  <TableCell className="text-muted-foreground">{group.description ?? "No description"}</TableCell>
                  <TableCell>{group.memberIds.length}</TableCell>
                </TableRow>
              ))}
              {groups.length === 0 && (
                <TableRow>
                  <TableCell colSpan={3} className="py-10 text-center text-muted-foreground">
                    No assignment groups available.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  )
}
