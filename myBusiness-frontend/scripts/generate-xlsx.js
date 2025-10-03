// scripts/generate-xlsx.js
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import * as XLSX from 'xlsx';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const root = path.resolve(__dirname, '..');
const inputCsv = path.join(root, 'sample-data', 'products-import.csv');
const outputXlsx = path.join(root, 'sample-data', 'products-import.xlsx');

if (!fs.existsSync(inputCsv)) {
  console.error('Input CSV not found:', inputCsv);
  process.exit(1);
}

const csv = fs.readFileSync(inputCsv, 'utf8');

// Read CSV into a workbook
const wb = XLSX.read(csv, { type: 'string' });

// Ensure friendly sheet name
const firstName = wb.SheetNames[0];
if (firstName && firstName !== 'Productos') {
  wb.Sheets['Productos'] = wb.Sheets[firstName];
  delete wb.Sheets[firstName];
  wb.SheetNames[0] = 'Productos';
}

XLSX.writeFile(wb, outputXlsx, { bookType: 'xlsx' });

console.log('OK: generated', outputXlsx);