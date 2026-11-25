alter table organizations add column trial_ends_at date;

-- Give existing organizations a 20-day trial from when they were created.
update organizations set trial_ends_at = (created_at::date + 20) where trial_ends_at is null;
