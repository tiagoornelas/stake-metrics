import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

export interface ExcelColumn {
  header: string;
  key: string;
}

export function generateExcel<T>(
  data: T[], 
  columns: ExcelColumn[], 
  filename: string
): void {
  const excelData = data.map(item => 
    columns.reduce((acc, column) => {
      acc[column.header] = (item as any)[column.key] ?? '';
      return acc;
    }, {} as Record<string, any>)
  );

  const worksheet = XLSX.utils.json_to_sheet(excelData);
  
  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, worksheet, 'Report');

  const excelBuffer = XLSX.write(workbook, { 
    bookType: 'xlsx', 
    type: 'array' 
  });

  const blob = new Blob([excelBuffer], { 
    type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
  });
  saveAs(blob, `${filename}.xlsx`);
}
