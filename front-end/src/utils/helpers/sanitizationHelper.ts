export const sanitizePhoneMask = (phone: string | undefined): string => `+55${phone?.replace(/\D/g, '')}`;

export const removeCountryPrefixFromPhone = (phone: string | undefined): string => phone?.replace("+55", "") || '';

export const applyPhoneMask = (phone: string | undefined): string => {
    if (!phone) return '';
    const phoneDigits: string = phone.replace("+55", "");
    return `(${phoneDigits.slice(0, 2)}) ${phoneDigits.slice(2)}`;
}

export const applyDateMask = (date: string | null | undefined): string => {
    if (!date) return '';
    const dateObj: Date = new Date(date);
    const day: string = String(dateObj.getDate()).padStart(2, '0');
    const month: string = String(dateObj.getMonth() + 1).padStart(2, '0');
    const year: number = dateObj.getFullYear();
    const hours: string = String(dateObj.getHours()).padStart(2, '0');
    const minutes: string = String(dateObj.getMinutes()).padStart(2, '0');
    return `${day}/${month}/${year} ${hours}:${minutes}`;
}