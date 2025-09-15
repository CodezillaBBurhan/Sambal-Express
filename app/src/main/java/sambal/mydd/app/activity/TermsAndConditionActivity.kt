package sambal.mydd.app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import sambal.mydd.app.R
import sambal.mydd.app.databinding.ActivityTermsAndConditionBinding
import sambal.mydd.app.utils.StatusBarcolor
import java.lang.Exception

class TermsAndConditionActivity : AppCompatActivity() {
    private var binding: ActivityTermsAndConditionBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_terms_and_condition)
        binding!!.done.setOnClickListener { finish() }
        binding!!.text.text = """
            Dealdio (we/us/our/the Company) is committed to protecting and respecting your privacy.
            
            This policy (together with our terms of use and any other documents referred to on it) sets out the basis on which any personal data we collect from you, or that you provide to us, will be processed, stored and disclosed by us. Please read the following carefully to understand our practices regarding your personal data and how we treat it. Unless otherwise stated in this policy, by visiting www.ddpoints.co.uk (our Site) or using the mobile application we operate (our App), you are deemed to have accepted the practices described in this policy.
            
            The Site and App are owned and operated by Dealdio (Company No. 11460931) of 1st Floor Braintree House, Braintree Road, Ruislip, United Kingdom, HA4 0EJ. Because we process personal data from our merchants as well as personal data from their end users on their behalf, Dealdio processes personal data as both a Data Controller and a Data Processor, as defined in the Directive and the General Data Protection Regulation (GDPR).
            
            The Site or App may, from time to time, contain links to and from the websites of our partner networks, advertisers and affiliates or to websites shared by other users. If you follow a link to any of these websites, please note that these websites have their own privacy policies and that we do not accept any responsibility or liability for these policies. Please check these policies before you submit any personal data to these websites.
            
            The effective date of this policy is 25 May 2020. We update this policy from time to time, so please check back regularly to stay informed of current privacy practices.
            
            
            1. Information we process about you
            
            Personal data, or personal information, means any information about an individual from which that person can be identified. It does not include data where the identity has been removed (anonymous data).
            
            We collect and process personal information so we can provide our service to you. It’s important that the personal data we hold about you is accurate and current. Please keep us informed if your personal data changes during your relationship with us..
            
            Please note that we do not collect any Special Categories of personal data about you (such as details about your race or ethnicity, religious or philosophical beliefs, sex life, political opinions, trade union membership, information about your health and genetic and biometric data), and we do not collect any information about criminal convictions and offences.
            
            The information we collect and process about you is explained in the sections below.
            
            1.1 Information you give us
            
            You provide information to us when you fill in forms on our Site or App, register to use our Site or App, subscribe to our service or marketing database, post material, search for a product or service, scan the QR code in a merchants store or premises using our mobile app, place an order on a website that utilizes DD Points, request further services, enter a competition or promotion, report a problem with our Site or App, complete a survey, or correspond with us (by phone, email or otherwise).
            
            The information you give us includes your name, email address or login details, phone number, payment information (such as bank information or credit card information), purchase history and details of transactions carried out through our Site or App and our merchants, spatial data, and photograph(s) (if you choose to use this feature on our mobile application)..
            
            We ask for your explicit consent to use and process the data described above. However, some of this data is necessary for the provision of our services and the fulfilment of our contractual obligations to you, and we may rely on this as a legal basis for using the data described above if necessary.
            
            1.2 Information we automatically collect
            
            Each time you visit or use our Site or App, we automatically collect the following information:
            
            • Technical information, including the Internet protocol (IP) address used to connect your computer to the Internet, your login information, browser type and version, time zone setting, location, network data, browser plug-in types and versions, languages, operating system and platform; and:
            
            • Information about your visit, including the full Uniform Resource Locators (URL) clickstream to, through and from the Site or App (including date and time), pages you viewed or searched for, page response times, download errors, length of visits to certain pages, page interaction information (such as scrolling, clicks, and mouse-overs), and methods used to browse away from the page and any phone number used to call our customer service number.
            
            We use this information as statistical data about our users’ browsing actions and patterns, for system administration, and to report information to third parties. Because we collect, use, and share this information in the aggregate, it does not identify any individual..
            
            1.3. Information we receive from third parties
            
            If you’re an end user of a merchant’s loyalty program, please note that we receive and send information about you to and from the merchant(s) whose loyalty program(s) you use.
            
            We ask for your explicit consent to use and process the data described above. However, some of this data is necessary for the provision of our services and the fulfillment of our contractual obligations to you, and we may rely on this as a legal basis for using the data described above if necessary.
            
            2. Legitimate bases
            
            We will ensure that your personal data is processed lawfully, fairly, and transparently and that it will only be processed if at least one of the following bases applies:
            
            • You have given your clear consent to the processing of your personal data for a specific purpose;
            
            • Processing is necessary for the performance of a contract to which you are a party (or for us to take steps at your request prior to entering into a contract with you);
            
            • Processing is necessary for our compliance with the law;
            
            • Processing is necessary for us to perform a task in the public interest or in the exercise of official authority and the task/function has a clear basis in law; or
            
            • Processing is necessary for our legitimate interests or the legitimate interests of a third party (except where there is a good reason to protect your personal data which overrides those legitimate interests), such as:
            
            • Allowing us to effectively and efficiently manage and administer the operation of our business;
            
            • Maintaining compliance with internal policies and procedures;
            
            • Monitoring the use of our copyrighted materials;
            
            • Offering optimal, up-to-date security solutions for mobile devices and IT systems; and
            
            • Obtaining further knowledge of current threats to network security in order to update our security solutions and provide these to the market.
            
            3. Cookies
            
            A cookie is a small file of letters and numbers that we store on your browser. Cookies contain information that is transferred to your computer’s hard drive (or the hard drive of another relevant device). We use cookies to distinguish you from other users on the Site and App, to help us provide you with a good experience when you browse by remembering your user preferences, and to help us improve the Site and App.
            
            Some of the cookies we use are essential for the Site or App to operate. If you use your browser settings to block all cookies (including essential cookies), you may not be able to access all or parts of our Site or App.
            
            Before any non-essential cookies are placed on your device, you will be shown a pop-up message requesting your consent to setting those cookies. By default, most internet browsers accept cookies, but you can choose to enable or disable some or all cookies via the settings on your internet browser. Most internet browsers also enable you to choose whether you wish to disable all cookies or only third party cookies. For further details, please consult the help menu in your internet browser.
            
            We use the following cookies:
            
            • Strictly necessary cookies. These cookies are required to save your session and to carry out other activities that are strictly necessary for the operation of the Site or App. They include, by way of general example, cookies that enable you to log into secure areas of the Site or App, use a shopping cart, or make use of e-billing services. These cookies are session cookies, which means they’re temporary and will expire when you close your browser.
            
            • Analytical/performance cookies. These cookies allow us to recognise and count the number of visitors and to see how visitors move around the Site or App when they’re using it. These cookies help us improve the way the Site or App works by, for example, ensuring that users are finding what they’re looking for easily.
            
            • Functionality cookies. These cookies are used to recognise you when you return to the Site or App. They enable us to personalise our content for you, greet you by name and remember your preferences.
            
            • Targeting cookies. These cookies record your visit to the Site or App, the pages you visit, and the links you follow. We use this information to make the Site and App more relevant to your interests.
            
            • Social Media cookies. These cookies work together with social media plug-ins. For example, when we embed photos, video and other content from social media websites, the embedded pages contain cookies from these websites. Similarly, if you choose to share our content on social media, a cookie may be set by the service you have chosen to share content through.
            
            • Third Party cookies. Please note that we do not control cookies placed by third parties and our Site does not block them.
            
            Where your personal data is stored
            
            4.1 Our storage and security
            
            All of your personal information is stored on our secure servers and we have put in place appropriate physical, electronic, and management procedures to safeguard and secure the data we collect. Customer files are protected with safeguards according to the sensitivity of the relevant information. Appropriate controls (such as restricted access) are placed on our computer systems. Physical access to areas where personal data is gathered, processed or stored is limited to authorised employees. Any payment transactions will be encrypted using SSL technology. Where you have a password which enables you to access certain parts of our Site or App, you are responsible for storing this password and keeping it confidential. We ask you not to share your password with anyone
            
            Unfortunately, the transmission of information via the internet is not completely secure. Although we will do our best to protect your personal data, we cannot guarantee the security of your data transmitted to our Site or App; any transmission is at your own risk. Once we have received your information, we will use strict procedures and security features to try to prevent unauthorised access.
            
            4.2 Third parties with whom you choose to share your data
            
            You may choose to share any information, photographs or other content that you voluntarily submit to the Site or App, and such data will become available and viewable by other merchants Once you have shared your content or made it public, that content may be re-shared by others.
            
            If you choose to connect to social media networks from our Site or App, or post any of your content on our Site or App to those networks, then, in accordance with your social media privacy settings, the personal information that you post, transmit, or otherwise make available on the social media platform may be viewed or used by other users of those networks. We have no control over such viewing and use and cannot prevent further use of such information by third parties.
            
            When you interact with us through social media networks, we access information about you held by that account (in accordance with your social media privacy settings). Any links to social media are not under our control and remain your sole responsibility. Any information posted via social media through our Site or App, or any third party you allow to access your content, is done entirely at your own risk. By posting to a public platform, you make that information visible to third parties who can use the information at their discretion.
            
            Always think carefully before disclosing personal data or other information to other users or merchants, or otherwise posting personal data on the Site or App. We do not control the contents of communications made between users and merchants (although you can make a complaint about another user or merchant via email at sales@ddpoints.co.uk).
            
            5. Uses made of the information
            
            We use information held about you in the following ways:
            
            • To administer and manage your account, provide you with information, products or services you request from us, and to carry out any other obligations arising from any contracts entered into between you and us.
            
            • To ensure that content from our Site and App is presented in the most effective manner for you and for your device.
            
            • To allow you to participate in interactive features of our service, when you choose to do so.
            
            • To respond to communications from you and to provide you with information about other products or services we offer that are similar to those you have already enquired about.
            
            • To administer the Site and App and for internal operations, including troubleshooting, data analysis, testing, research, statistical and survey purposes.
            
            • As part of our efforts to keep the Site and App safe and secure, e.g. by conducting analysis required to detect malicious data and understand how this may affect your IT system.
            
            • To notify you about changes to our service..
            
            We will not send you any unsolicited marketing or spam, and we will take all reasonable steps to ensure that we fully protect your rights and comply with our obligations under applicable Data Protection Laws, meaning:.
            
            • The General Data Protection Regulation (EU 2016/679) (GDPR) (unless and until it is no longer directly applicable in the UK) and any national implementing laws, regulations and secondary legislation, as amended or updated from time to time, in the UK, and
            
            • Any successor legislation to the GDPR or the Data Protection Act 1998.
            
            6. How long we store your information
            
            We only keep your personal information for as long as it’s necessary for our original legitimate purpose for collecting the information, for as long as we have your permission to keep it and/or for any periods of time prescribed by law or regulation. If you are a consumer or end user, we will delete your personal information no later than 30 days after you delete your account or send a deletion request to sales@ddpoints.co.uk. If you are a merchant, we will delete your personal information no later than 1 year after you terminate use of our Service.
            
            7. Disclosure of your information
            
            We require all third parties to respect the security of your personal data and to treat it in accordance with the law.
            
            If you’re an end user of a merchant’s loyalty program, your information is automatically shared with the merchant(s) whose loyalty program(s) you use.
            
            • Business partners, suppliers and sub-contractors for the performance of any contract we enter into with them or you. Your personal data will only be transferred to a business partner who is contractually obliged to comply with appropriate data protection obligations and the relevant privacy and confidentiality obligations.
            
            We are permitted share your personal information with any member of our group, which means any subsidiaries or any ultimate holding company and its subsidiaries, as defined in section 1159 of the UK Companies Act 2006. We are also permitted disclose your personal information to third parties (without contacting you in advance or seeking your consent) in the following circumstances:
            
            • In the event that we sell or buy any business or assets, in which case we may disclose your personal data to the prospective seller or buyer of such business or assets;
            
            • If DD Points or substantially all of its assets are acquired by a third party, in which case personal data will be one of the transferred assets and the purchaser will be permitted to use the data for the purposes for which it was originally collected by us; and
            
            • If we are under a duty to disclose or share your personal data in order to comply with any legal obligation, or in order to enforce or apply our Website Terms of Use and other agreements, or to protect the rights, property, or safety of DD Points, our customers, or others. This includes exchanging information with other companies and organisations for the purposes of fraud protection and credit risk reduction.
            
            8. Your rights
            
            Under the GDPR, you have the right to:
            
            • Withdraw your consent to the processing of your personal data at any time. Please note, however, that we may still be entitled to process your personal data if we have another legitimate reason for doing so (such as to comply with a legal obligation).
            
            • Be informed of what data we hold and the purpose for processing the data, as a whole or in parts.
            
            • Be forgotten and, in some circumstances, have your data erased by ourselves and our affiliates (although this is not an absolute right and there may be circumstances where you ask us to erase your personal data but we are legally entitled to retain it).
            
            • Correct or supplement any information we hold about you that is incorrect or incomplete.
            
            •Restrict processing of the information we hold about you (for example, so that inaccuracies may be corrected—but again, there may be circumstances where you ask us to restrict processing of your personal data but we are legally entitled to refuse that request).
            
            • Object to the processing of your data.
            
            • Obtain your data in a portable manner and reuse the information we hold about you.
            
            • Challenge any data we use for the purposes of automated decision-making and profiling (in certain circumstances—as above, there may be circumstances where you ask us to restrict our processing of your personal data but we are legally entitled to refuse that request).
            
            • Complain to a supervisory authority if you think any of your rights have been infringed by us. (We would, however, appreciate the chance to address your concerns, so please contact us prior to taking this step). In Europe, the supervisory authority is the Information Commissioner’s Office (ICO), details below:
            
            – Information Commissioner’s Office (ICO) in the UK: https://ico.org.uk/global/contact-us/
            
            – The rest of the EEA: https://edpb.europa.eu/about-edpb/board/members_en
            
            You will not have to pay a fee to access your personal data (or to exercise any of the other rights) unless your request is clearly unfounded, repetitive or excessive. Alternatively, we may refuse to comply with your request in these circumstances.
            
            You have the right to ask us not to process your personal data for marketing purposes. We will get your express opt-in consent before we use your data for such purposes or share your personal data with any third parties for such purposes. You can exercise your right to prevent such processing by contacting us at the Company Address, via email at sales@ddpoints.co.uk, by phone at +44 (0)20 7183 0513 or by unsubscribing using the links contained in the marketing emails.
            
            You may revoke your consent for us to use your personal data as described in this privacy policy at any time by emailing us at sales@ddpoints.co.uk and we will delete your data from our systems. To enforce any of the above rights, please contact us at our Company Address or via email at sales@ddpoints.co.uk. If you wish to enquire about the personal data any of our merchants hold about you, please contact that merchant.
            
            We will notify you and any applicable regulator of a breach of your personal data when we are legally required to do so.
            
            9. Changes to our privacy policy
            
            Any changes we may make to our privacy policy in the future will be posted on this page and, where appropriate, notified to you by email. You will be deemed to have accepted the terms of the updated Privacy & Cookie Policy on your first use of the Site or App following the alterations. Please check back frequently to see any updates or changes to our Privacy Policy.
            
            10. Contact
            
            Questions, comments and requests regarding this privacy policy are welcomed and should be addressed to our Company Address or to our email at sales@ddpoints.co.uk.
            
            
            
            """.trimIndent()
    }

    public override fun onResume() {
        super.onResume()
        try {
            StatusBarcolor.setStatusbarColor(this@TermsAndConditionActivity, "")
        } catch (e: Exception) {
        }
    }
}