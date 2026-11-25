-- Departments are now Teams: every employee belongs to a team with one manager.
alter table departments rename to teams;
alter table teams rename constraint uk_departments_org_name to uk_teams_org_name;
alter index idx_departments_organization rename to idx_teams_organization;

alter table employees rename column department_id to team_id;
alter index idx_employees_department rename to idx_employees_team;
