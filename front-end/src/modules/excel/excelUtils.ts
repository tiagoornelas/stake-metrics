import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

export interface ExcelColumn {
  header: string;
  key: string;
  width?: number;
}

export interface ExcelSheet {
  name: string;
  data: Record<string, any>[];
  columns: ExcelColumn[];
}

export function generateExcel<T>(
  data: T[],
  columns: ExcelColumn[],
  filename: string,
  additionalSheets?: ExcelSheet[]
): void {
  const workbook = XLSX.utils.book_new();

  const mainExcelData = data.map(item =>
    columns.reduce((acc, column) => {
      acc[column.header] = (item as any)[column.key] ?? '';
      return acc;
    }, {} as Record<string, any>)
  );

  const mainWorksheet = XLSX.utils.json_to_sheet(mainExcelData);

  if (columns.some(col => col.width !== undefined)) {
    mainWorksheet['!cols'] = columns.map(col => ({ wch: col.width }));
  }

  XLSX.utils.book_append_sheet(workbook, mainWorksheet, 'Report');

  if (additionalSheets && additionalSheets.length > 0) {
    additionalSheets.forEach(sheet => {
      const sheetData = sheet.data.map(item =>
        sheet.columns.reduce((acc, column) => {
          acc[column.header] = item[column.key] ?? '';
          return acc;
        }, {} as Record<string, any>)
      );

      const worksheet = XLSX.utils.json_to_sheet(sheetData);

      if (sheet.columns.some(col => col.width !== undefined)) {
        worksheet['!cols'] = sheet.columns.map(col => ({ wch: col.width }));
      }

      XLSX.utils.book_append_sheet(workbook, worksheet, sheet.name);
    });
  }

  const excelBuffer = XLSX.write(workbook, {
    bookType: 'xlsx',
    type: 'array'
  });

  const blob = new Blob([excelBuffer], {
    type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  });
  saveAs(blob, `${filename}.xlsx`);
}