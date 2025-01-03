import React, { createContext, useState, useContext } from 'react';

import translations from '../translations/translations.json';

type Language = 'pt' | 'en';

interface TranslationContextType {
    language: Language;
    setLanguage: (lang: Language) => void;
    t: (key: string, params?: Record<string, any>) => string;
}

const TranslationContext = createContext<TranslationContextType>({
    language: 'pt',
    setLanguage: () => {},
    t: () => ''
});

export const TranslationProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [language, setLanguage] = useState<Language>(() => {
        const browserLang = navigator.language.split('-')[0] as Language;
        return browserLang === 'en' ? 'en' : 'pt';
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
        
        // Interpolate parameters if provided
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

export const useTranslation = () => useContext(TranslationContext);
