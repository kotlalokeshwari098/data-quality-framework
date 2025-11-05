import {defineConfig} from 'vitepress'

export default defineConfig({
    title: 'Data Quality Framework',
    description: 'Comprehensive framework for monitoring and ensuring data quality in biomedical research environments',
    base: '/data-quality-framework/',

    sitemap: {
        hostname: 'https://bbmri-cz.github.io/data-quality-framework/'
    },

    themeConfig: {
        logo: '/logo.svg',

        nav: [
            {text: 'Home', link: '/'},
            {text: 'User Guide', link: '/user/'},
            {text: 'Developer Guide', link: '/developer/'}
        ],

        sidebar: {
            '/user/': [
                {
                    text: 'User Guide',
                    items: [
                        {text: 'Overview', link: '/user/'},
                        {text: 'Getting Started', link: '/user/getting-started'},
                        {text: 'Privacy and Security', link: '/user/privacy'},
                        {text: 'Deployment', link: '/user/deployment'}
                    ]
                }
            ],
            '/developer/': [
                {
                    text: 'Developer Guide',
                    items: [
                        {text: 'Overview', link: '/developer/'},
                        {text: 'Contributing', link: '/developer/contributing'},
                    ]
                }
            ]
        },

        socialLinks: [
            {icon: 'github', link: 'https://github.com/bbmri-eric/data-quality-framework'}
        ],

        editLink: {
            pattern: 'https://github.com/bbmri-eric/data-quality-framework/edit/main/docs/:path',
            text: 'Edit this page on GitHub'
        },

        footer: {
            message: 'Licensed under the GNU GPL v3.0',
            copyright: 'Copyright © 2025 BBMRI-ERIC®'
        },

        search: {
            provider: 'local'
        },

        lastUpdated: {
            text: 'Last updated',
            formatOptions: {
                dateStyle: 'medium',
                timeStyle: 'short'
            }
        }
    },

    markdown: {
        lineNumbers: true,
        linkify: true,
        config: (md) => {
            // Add any markdown-it plugins here if needed
        }
    },

    head: [
        ['link', {rel: 'icon', href: 'favicon.ico'}],
        ['link', {rel: 'alternate icon', href: 'favicon.ico'}],
        ['meta', {name: 'theme-color', content: '#667eea'}],
        ['meta', {name: 'viewport', content: 'width=device-width, initial-scale=1.0'}],
        ['meta', {property: 'og:type', content: 'website'}],
        ['meta', {property: 'og:locale', content: 'en'}],
        ['meta', {property: 'og:title', content: 'Data Quality Framework | Biomedical Data Quality Monitoring'}],
        ['meta', {property: 'og:site_name', content: 'Data Quality Framework'}],
        ['meta', {property: 'og:image', content: '/logo.svg'}],
        ['meta', {property: 'og:url', content: 'https://bbmri-cz.github.io/data-quality-framework/'}],
        ['meta', {
            property: 'og:description',
            content: 'Open-source, privacy-preserving framework for monitoring and ensuring data quality in biomedical research environments'
        }],
        ['meta', {name: 'twitter:card', content: 'summary_large_image'}],
        ['meta', {name: 'twitter:image', content: '/logo.svg'}]
    ]
})
