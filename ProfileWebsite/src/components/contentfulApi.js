import { createClient } from 'contentful';

const client = createClient({
  space: 'YOUR_SPACE_ID',
  accessToken: 'YOUR_ACCESS_TOKEN',
});

export async function fetchCMSPosts() {
  const entries = await client.getEntries({ content_type: 'blogPost' });
  return entries.items.map(item => ({
    id: item.sys.id,
    ...item.fields,
  }));
}
