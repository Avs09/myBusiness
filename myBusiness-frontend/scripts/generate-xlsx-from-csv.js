// scripts/generate-xlsx-from-csv.js
// Usage:
//   node scripts/generate-xlsx-from-csv.js sample-data/products-import-by-id.csv sample-data/products-import-by-id.xlsx
//
// Reads a CSV file (headers required) and writes an XLSX with a single sheet named "Productos".

import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import * as XLSX from 'xlsx';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const root = path.resolve(__dirname, '..');

const inArg = process.argv[2] || 'sample-data/products-import-by-id.csv';
const outArg = process.argv[3] || 'sample-data/products-import-by-id.xlsx';

const inputCsv = path.resolve(root, inArg);
const outputXlsx = path.resolve(root, outArg);

if (!fs.existsSync(inputCsv)) {
  console.error('[generate-xlsx-from-csv] Input CSV not found:', inputCsv);
  process.exit(1);
}

const csv = fs.readFileSync(inputCsv, 'utf8');

// Create workbook from CSV
const wb = XLSX.read(csv, { type: 'string' });

// Normalize sheet name to "Productos"
const firstName = wb.SheetNames[0];
if (firstName && firstName !== 'Productos') {
  wb.Sheets['Productos'] = wb.Sheets[firstName];
  delete wb.Sheets[firstName];
  wb.SheetNames[0] = 'Productos';
}

XLSX.writeFile(wb, outputXlsx, { bookType: 'xlsx' });

console.log('[generate-xlsx-from-csv] OK:', outputXlsx);