import React, { createContext, useState } from 'react';
import translations from '../translations/translations.json';

type Language = 'pt' | 'en';

export interface TranslationContextType {
    language: Language;
    setLanguage: (lang: Language) => void;
    t: (key: string, params?: Record<string, any>) => string;
}

export const TranslationContext = createContext<TranslationContextType>({
    language: 'pt',
    setLanguage: () => {},
    t: () => ''
});

const TranslationProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [language, setLanguage] = useState<Language>(() => {
        // const browserLang = navigator.language.split('-')[0] as Language;
        // TODO: Temporarily set portuguese as default
        // return browserLang === 'en' ? 'en' : 'pt';
        return 'pt';
    });

    const t = (key: string, params?: Record<string, any>) => {
        const keys = key.split('.');
        let value: any = translations;
        
        for (const k of keys) {
            value = value?.[k];
            if (value === undefined) return key;
        }
        
        if (typeof value === 'object' && value.hasOwnProperty(language)) {
            value = value[language];
        }
        
        if (params && typeof value === 'string') {
            return value.replace(/\{(\w+)\}/g, (match, p1) => 
                params.hasOwnProperty(p1) ? params[p1] : match
            );
        }
        
        return value || key;
    };

    return (
        <TranslationContext.Provider value={{ language, setLanguage, t }}>
            {children}
        </TranslationContext.Provider>
    );
};

export default TranslationProvider;
