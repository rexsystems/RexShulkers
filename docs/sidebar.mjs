const root = "rexshulkers";

export const rexshulkers = {
  label: "RexShulkers",
  items: [
    { label: "Overview", slug: root },
    {
      label: "Getting started",
      items: [
        { label: "Installation", slug: `${root}/getting-started/installation` },
        { label: "Requirements", slug: `${root}/getting-started/requirements` },
      ],
    },
    {
      label: "Guides",
      items: [
        { label: "Filled shulkers in crates", slug: `${root}/guides/giving-filled-shulkers` },
      ],
    },
    {
      label: "Configuration",
      items: [
        { label: "Settings", slug: `${root}/configuration/overview` },
        { label: "Messages", slug: `${root}/configuration/messages` },
      ],
    },
    {
      label: "Reference",
      items: [
        { label: "Commands", slug: `${root}/reference/commands` },
        { label: "Permissions", slug: `${root}/reference/permissions` },
        { label: "Placeholders", slug: `${root}/reference/placeholders` },
      ],
    },
    { label: "FAQ", slug: `${root}/faq` },
  ],
};
