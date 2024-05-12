const TabKeys = {
  DESCRIPTION: "description",
  SUBMIT: "submit",
} as const;

export const findTabKey = (
  tab: string
): (typeof TabKeys)[keyof typeof TabKeys] => {
  const found = Object.keys(TabKeys).find(
    (key) => TabKeys[key as keyof typeof TabKeys] === tab
  ) as keyof typeof TabKeys;
  return TabKeys[found];
};

export default TabKeys;
