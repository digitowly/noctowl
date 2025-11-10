# Strategies for scraping information from Wikipedia

## By Infobox

Wikipedia articles often feature infoboxes containing structured scientific information in a table view.

For biological species (animals, plants, etc.), these infoboxes typically include taxonomic classification data.

The scraping process:

1. Access species Wikipedia page
2. Locate infobox via CSS selector (`.infobox`)
3. Extract taxonomy fields (kingdom, phylum, class, order, etc.)
4. Parse and structure the taxonomic hierarchy

This efficient method quickly retrieves standardized taxonomy information, though availability might vary across
articles, making it not a hundred percent reliable.

```mermaid
---
title: Check taxonomy data on wikipedia
---
flowchart TD
    A[Get next page] --> B[Has infobox?]
    B -- No --> A
    B -- Yes --> C[Has infobox with taxonomy data?]
    C -- No --> A
    C -- Yes --> D[Extract taxonomy fields]
````
