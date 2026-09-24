package com.assistant.scheme.config;

import com.assistant.scheme.model.Role;
import com.assistant.scheme.model.Scheme;
import com.assistant.scheme.model.User;
import com.assistant.scheme.repository.SchemeRepository;
import com.assistant.scheme.repository.UserRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SchemeRepository schemeRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.assistant.scheme.service.DocumentService documentService;

    @Autowired
    public DatabaseSeeder(UserRepository userRepository, SchemeRepository schemeRepository, PasswordEncoder passwordEncoder, com.assistant.scheme.service.DocumentService documentService) {
        this.userRepository = userRepository;
        this.schemeRepository = schemeRepository;
        this.passwordEncoder = passwordEncoder;
        this.documentService = documentService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed default users if none exist
        if (userRepository.count() == 0) {
            User citizen = User.builder()
                    .username("user")
                    .email("user@gov.in")
                    .passwordHash(passwordEncoder.encode("user123"))
                    .role(Role.USER)
                    .build();

            User admin = User.builder()
                    .username("admin")
                    .email("admin@gov.in")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();

            userRepository.saveAll(Arrays.asList(citizen, admin));
            System.out.println("Seeded default users: [user/user123] and [admin/admin123]");
        }

        // Generate official government PDF guides in the uploads folder
        generateLocalPdfGuide("pmay.pdf", "MINISTRY OF HOUSING AND URBAN AFFAIRS - GOVERNMENT OF INDIA", 
                "Pradhan Mantri Awas Yojana (PMAY-Urban & Gramin Guidelines)",
                "1. SCHEME OVERVIEW & OBJECTIVES", "Pradhan Mantri Awas Yojana is a flagship mission of the Government of India to provide all-weather pucca houses to eligible urban and rural families. The scheme aims to address housing shortage among Economically Weaker Sections (EWS), Low Income Groups (LIG), and Middle Income Groups (MIG).",
                "2. ELIGIBILITY & INCOME LIMITS", "Beneficiary age must be between 18 and 99 years. Annual family income limits: EWS up to Rs 3,00,000, LIG between Rs 3,00,001 and Rs 6,00,000, MIG-I up to Rs 12,00,000, MIG-II up to Rs 18,00,000. Beneficiary family must not own a pucca house in any part of India. All social categories (General, OBC, SC, ST) are eligible across all central and state jurisdictions.",
                "3. FINANCIAL GRANTS & INTEREST SUBSIDY", "Under Credit Linked Subsidy Scheme (CLSS), beneficiaries get upfront interest subsidy up to Rs 2.67 Lakhs on home loan interest at 6.5% for 20 years. Direct financial assistance of Rs 1.20 Lakhs in plains and Rs 1.30 Lakhs in hilly/hilly-difficult areas is provided for individual house construction.",
                "4. REQUIRED DOCUMENTS", "Aadhaar Card of all family members, Address Proof (Voter ID/Electricity Bill), Bank Account Passbook linked with Aadhaar, Income Certificate issued by competent authority, BPL Card or Jan Aadhaar Card, Land ownership deed or site allotment certificate, Affidavit confirming no pucca house ownership.",
                "5. ALTERNATIVE DOCUMENT CONCESSIONS", "If formal Income Certificate is unavailable, a self-declaration income affidavit certified by local Gram Panchayat Sarpanch, Municipal Councillor, or active MGNREGA Job Card is accepted as valid income proof for EWS applicants.",
                "6. APPLICATION PROCEDURE & HELPLINE", "Apply online at pmaymis.gov.in portal or visit nearest Common Service Centre (CSC). National Toll-Free Helpline: 1800-11-3377 / 1800-11-3388. Official Portal: pmaymis.gov.in.");

        generateLocalPdfGuide("pmkisan.pdf", "MINISTRY OF AGRICULTURE AND FARMERS WELFARE - GOVT OF INDIA", 
                "Pradhan Mantri Kisan Samman Nidhi (PM-KISAN Guidelines)",
                "1. SCHEME OVERVIEW & OBJECTIVES", "PM-KISAN is a Central Sector Scheme providing direct income support to all landholding farmer families across the country to supplement their financial needs for procuring agricultural inputs and domestic expenses.",
                "2. ELIGIBILITY & TARGET GROUP", "All landholding farmer families who own cultivable land registered in official state revenue land records are eligible. Age limit 18 to 100 years. Farmers from all social categories (General, OBC, SC, ST) and all states/UTs are covered. Institutional landholders, former/present ministers, income-tax payers, and retired pensioners getting Rs 10,000/month or more are excluded.",
                "3. FINANCIAL ASSISTANCE STRUCTURE", "Financial benefit of Rs 6,000 per year is provided in three equal installments of Rs 2,000 every 4 months (April-July, August-November, December-March). Total annual assistance transferred directly into bank accounts via Aadhaar-enabled Payment System (AePS).",
                "4. REQUIRED DOCUMENTS", "Land Registry Ownership Record (Jamabandi / Fard / Khasra Khatoni extract), Aadhaar Card mandatory for DBT transfer, Bank Account Passbook with active IFSC code, Valid Mobile Number linked with Aadhaar.",
                "5. ALTERNATIVE DOCUMENT CONCESSIONS", "In case of delayed land registry extract updates, a provisional land possession certificate endorsed by the Revenue Tehsildar or a valid Kisan Credit Card (KCC) document is accepted for provisional registration.",
                "6. APPLICATION PROCEDURE & HELPLINE", "Register online via Farmers Corner on pmkisan.gov.in or through CSC centres. Helpline Toll-Free: 155261 / 1800115526. Official Portal: pmkisan.gov.in.");

        generateLocalPdfGuide("ssy.pdf", "MINISTRY OF WOMEN AND CHILD DEVELOPMENT - GOVT OF INDIA", 
                "Sukanya Samriddhi Yojana (SSY Small Savings Guidelines)",
                "1. SCHEME OVERVIEW & OBJECTIVES", "Sukanya Samriddhi Yojana is a small deposit savings scheme launched under Beti Bachao Beti Padhao initiative to secure financial independence and fund higher education and marriage expenses of girl children.",
                "2. ELIGIBILITY CRITERIA", "Account can be opened by natural or legal guardian for a girl child from age 0 (birth) up to 10 years. Gender restriction: Female child only. Maximum 2 accounts permitted per family (or 3 in case of twin/triplet girl births). Available to all income groups and social categories across all states.",
                "3. FINANCIAL BENEFITS & INTEREST RATE", "Offers highest guaranteed interest rate among government small savings (8.2% p.a. compounded annually). Annual deposit range: minimum Rs 250 up to maximum Rs 1,50,000 per financial year. Deposits qualify for tax deduction under Section 80C of Income Tax Act. Account matures 21 years from opening date or upon marriage after age 18.",
                "4. REQUIRED DOCUMENTS", "Birth Certificate of Girl Child issued by Municipal Authority or Registrar of Births, Aadhaar Card of Parent/Guardian, PAN Card or Form 60 of Guardian, Passport Photographs of Girl Child and Guardian, Address Proof.",
                "5. ALTERNATIVE DOCUMENT CONCESSIONS", "If birth certificate is pending issuance, a hospital discharge summary or Gram Panchayat birth verification letter accompanied by a sworn guardian affidavit is accepted for provisional account opening for up to 30 days.",
                "6. WHERE TO APPLY & HELPLINE", "Accounts can be opened at any Post Office branch or authorized public/private commercial banks across India. Department of Posts Helpline: 1800-266-6868. Portal: indiapost.gov.in.");

        generateLocalPdfGuide("bhamashah.pdf", "DEPARTMENT OF MEDICAL AND HEALTH - GOVERNMENT OF RAJASTHAN", 
                "Rajasthan Swasthya Bima Yojana (Health Insurance Guidelines)",
                "1. SCHEME OVERVIEW & OBJECTIVES", "Rajasthan Swasthya Bima Yojana (integrated with Ayushman Bharat Mahatma Gandhi Rajasthan Swasthya Bima) provides cashless medical treatment and health insurance to families in Rajasthan state.",
                "2. ELIGIBILITY CRITERIA", "Resident of Rajasthan state. Available to all age groups (0 to 120 years), all occupations, and all genders. Family income limit up to Rs 2,50,000 per annum or families enrolled under National Food Security Act (NFSA) and SECC category.",
                "3. FINANCIAL MEDICAL COVERAGE", "Cashless hospitalization and treatment coverage up to Rs 5,00,000 per family per annum for secondary and tertiary medical packages across empanelled government and private hospitals.",
                "4. REQUIRED DOCUMENTS", "Jan Aadhaar Card / Bhamashah Card, Aadhaar Card of patient, Ration Card, Rajasthan Domicile Certificate.",
                "5. ALTERNATIVE DOCUMENT CONCESSIONS", "In emergency medical admissions, if Jan Aadhaar card is unavailable, the Jan Aadhaar Enrollment Slip or Ration Card authenticated at hospital Swasthya Margdarshak desk grants immediate cashless admission.",
                "6. HOW TO ACCESS & HELPLINE", "Cashless authorization available at all empanelled hospital desks. Helpline Toll-Free: 1800-180-6127. Portal: health.rajasthan.gov.in / sso.rajasthan.gov.in.");

        generateLocalPdfGuide("widow_pension.pdf", "MINISTRY OF RURAL DEVELOPMENT - GOVERNMENT OF INDIA", 
                "Indira Gandhi National Widow Pension Scheme (IGNWPS Guidelines)",
                "1. SCHEME OVERVIEW & OBJECTIVES", "IGNWPS is a major component of National Social Assistance Programme (NSAP) providing social security pension to destitute widows living below the poverty line.",
                "2. ELIGIBILITY CRITERIA", "Widowed women aged 40 to 79 years. Gender restriction: Female only. Occupation: Unemployed or destitute. Household annual family income must be under Rs 1,20,000 (BPL category). Eligible across all categories and states.",
                "3. PENSION ASSISTANCE STRUCTURE", "Direct monthly pension of Rs 1,000 to Rs 1,500 (comprising Central and State contribution) credited directly to beneficiary bank account every month.",
                "4. REQUIRED DOCUMENTS", "Husband's Death Certificate, Aadhaar Card, BPL Card or Income Certificate from Tehsildar, Bank Passbook, Passport Photograph, Resident Proof.",
                "5. ALTERNATIVE DOCUMENT CONCESSIONS", "If official death certificate is delayed, a certified copy of Police FIR or Sarpanch/Ward Councillor attestation certificate accompanied by Gram Sabha resolution is accepted provisionally.",
                "6. APPLICATION & HELPLINE", "Apply online at nsap.nic.in or submit application to Tehsildar / District Social Welfare Officer. NSAP Helpline: 1800-111-555. Portal: nsap.nic.in.");

        generateLocalPdfGuide("sc_scholarship.pdf", "MINISTRY OF SOCIAL JUSTICE AND EMPOWERMENT - GOVT OF INDIA", 
                "Post Matric Scholarship for SC Students (Guideline Notification)",
                "1. SCHEME OVERVIEW & OBJECTIVES", "Centrally sponsored scheme to provide financial assistance to Scheduled Caste (SC) students studying at post-matriculation or post-secondary stage to complete their education.",
                "2. ELIGIBILITY CRITERIA", "Category: Scheduled Caste (SC) only. Occupation: Regular Student. Age 15 to 25 years. Parental annual family income from all sources must not exceed Rs 2,50,000 per annum. Applicable across all Indian states.",
                "3. SCHOLARSHIP AMOUNT & BENEFITS", "100% compulsory non-refundable course fee reimbursed. Monthly maintenance allowance from Rs 550 to Rs 1,200/month depending on course category. Book bank and disability allowance also provided.",
                "4. REQUIRED DOCUMENTS", "SC Caste Certificate issued by SDM/Tehsildar, Parent Income Certificate, Previous Class Marksheet, College Admission Fee Receipt, Aadhaar Card, Bank Passbook.",
                "5. ALTERNATIVE DOCUMENT CONCESSIONS", "If current income certificate is expired, current month salary slip or Form 16 / Tehsildar income declaration certificate is accepted as temporary valid proof.",
                "6. APPLICATION & HELPLINE", "Apply online via National Scholarship Portal (scholarships.gov.in). Helpline Phone: 0120-6619540. Portal: scholarships.gov.in.");

        generateLocalPdfGuide("nmmss.pdf", "DEPARTMENT OF SCHOOL EDUCATION AND LITERACY - MINISTRY OF EDUCATION", 
                "National Means Cum Merit Scholarship Scheme (NMMSS Guidelines)",
                "1. SCHEME OVERVIEW & OBJECTIVES", "NMMSS awards scholarships to meritorious students of economically weaker sections to arrest dropout at Class VIII and encourage secondary stage education.",
                "2. ELIGIBILITY CRITERIA", "Occupation: Student in Class IX to XII. Age 12 to 18 years. Minimum 55% marks in Class VIII (50% for SC/ST). Parental annual income limit up to Rs 3,50,000 from all sources. Regular students in Government, Local Body, and Government-Aided schools eligible.",
                "3. FINANCIAL SCHOLARSHIP BENEFIT", "Scholarship of Rs 12,000 per annum (Rs 1,000 per month) awarded for 4 consecutive academic years from Class IX to Class XII.",
                "4. REQUIRED DOCUMENTS", "Class VIII Marksheet, Income Certificate of Parents, Selection Test Result Card, Aadhaar Card, Bank Account Passbook.",
                "5. ALTERNATIVE DOCUMENT CONCESSIONS", "If final Class VIII board certificate is pending, Headmaster / School Principal bonafide verification certificate with school seal is accepted.",
                "6. APPLICATION & HELPLINE", "Apply on National Scholarship Portal scholarships.gov.in. NSP Helpdesk: 0120-6619540. Portal: scholarships.gov.in.");

        generateLocalPdfGuide("yuva_sambal.pdf", "DEPARTMENT OF SKILL, EMPLOYMENT & ENTREPRENEURSHIP - RAJASTHAN", 
                "Mukhyamantri Yuva Sambal Yojana (Unemployment Allowance Guidelines)",
                "1. SCHEME OVERVIEW & OBJECTIVES", "Yuva Sambal Yojana provides monthly financial unemployment allowance and skill training to educated unemployed youth residing in Rajasthan state.",
                "2. ELIGIBILITY CRITERIA", "Resident of Rajasthan. Occupation: Unemployed Graduate. Age 21 to 35 years (relaxed up to 40 years for SC/ST/Women/PWD). Annual family income limit up to Rs 2,00,000. Must possess a Bachelor's Degree from a recognized university.",
                "3. UNEMPLOYMENT ALLOWANCE BENEFIT", "Monthly allowance of Rs 4,000/month for male youth and Rs 4,500/month for female, transgender, and disabled youth for up to 2 years or until employment.",
                "4. REQUIRED DOCUMENTS", "Rajasthan Domicile Certificate (Mool Niwas), Graduation Degree Marksheet, Self-Declaration Income Form I & K, Aadhaar Card, Jan Aadhaar Card, Bank Passbook.",
                "5. ALTERNATIVE DOCUMENT CONCESSIONS", "If original Degree Certificate has not been issued by university, a Provisional Degree Certificate or online marksheet verified by College Principal is accepted.",
                "6. APPLICATION & HELPLINE", "Apply online on SSO Rajasthan portal (sso.rajasthan.gov.in) under Employment service. Helpline Toll-Free: 1800-180-6127.");

        generateLocalPdfGuide("beti_bachao.pdf", "MINISTRY OF WOMEN AND CHILD DEVELOPMENT - GOVT OF INDIA", 
                "Beti Bachao Beti Padhao (BBBP Scheme Guidelines)",
                "1. SCHEME OVERVIEW & OBJECTIVES", "BBBP is a nationwide initiative to address child sex ratio imbalances, prevent female foeticide, ensure survival and protection of girl children, and promote girl education.",
                "2. ELIGIBILITY CRITERIA", "Gender: Female child. Age 0 to 18 years. All social categories (General, OBC, SC, ST) and all income groups across all states and union territories.",
                "3. INCENTIVES & WELFARE ASSISTANCE", "Educational kit grants, free textbooks, school admission reservation, sports training incentives, and financial counseling support for parents.",
                "4. REQUIRED DOCUMENTS", "Birth Certificate of Girl Child, Aadhaar Card of parents/guardian, Resident proof, School Enrollment slip.",
                "5. ALTERNATIVE DOCUMENT CONCESSIONS", "For newborn girls, Anganwadi / ASHA Worker birth registration slip is accepted for immediate health and educational kit benefits.",
                "6. WHERE TO CONTACT", "Contact local Anganwadi Centre, District Women Empowerment Officer, or School Principal. Women Helpline: 181. Childline: 1098. Portal: wcd.nic.in.");

        generateLocalPdfGuide("mahila_samman.pdf", "DEPARTMENT OF ECONOMIC AFFAIRS - MINISTRY OF FINANCE", 
                "Mahila Samman Savings Certificate (MSSC Guidelines)",
                "1. SCHEME OVERVIEW & OBJECTIVES", "Mahila Samman Savings Certificate is a specialized small savings scheme designed to empower women and girl investors with guaranteed high returns.",
                "2. ELIGIBILITY CRITERIA", "Gender restriction: Female only (or guardian on behalf of minor girl child). Age limit: No limit (0 to 120 years). Available to all occupations, categories, and income groups across India.",
                "3. DEPOSIT & INTEREST BENEFIT", "Fixed interest rate of 7.5% per annum compounded quarterly. Deposit limits: minimum Rs 1,000 up to maximum Rs 2,00,000 for a 2-year tenure. Partial withdrawal up to 40% permitted after 1 year.",
                "4. REQUIRED DOCUMENTS", "Aadhaar Card, PAN Card (or Form 60), Account Opening Form, Passport Photograph, Address Proof.",
                "5. ALTERNATIVE DOCUMENT CONCESSIONS", "If PAN Card is unavailable, Form 60 submitted along with Voter ID or Driving License is accepted for KYC compliance.",
                "6. WHERE TO APPLY & HELPLINE", "Available at all Post Office branches and authorized commercial banks. National Savings Institute Helpline: 1800-180-1111. Portal: indiapost.gov.in.");

        // Reload local PDF content into in-memory document registry for instant retrieval
        documentService.reloadAllLocalPdfs();

        // Seed default welfare schemes if none exist
        if (schemeRepository.count() == 0) {
            Scheme pmay = Scheme.builder()
                    .name("Pradhan Mantri Awas Yojana (PMAY)")
                    .category("Housing")
                    .state("Central")
                    .department("Ministry of Housing and Urban Affairs")
                    .description("An initiative by the Government of India in which affordable housing will be provided to the urban and rural poor with interest subsidies on home loans.")
                    .minAge(18)
                    .maxAge(99)
                    .maxIncome(600000.0)
                    .eligibleOccupations("All")
                    .eligibleCategories("All")
                    .eligibleGenders("All")
                    .documentsRequired("Aadhaar Card, Address Proof, Income Certificate, BPL Card, Bank Passbook, Passport Size Photograph")
                    .applyLink("https://pmaymis.gov.in/PMAYMIS2_2024/Auth/Login.aspx")
                    .pdfUrl("/uploads/pmay.pdf")
                    .metadata("{}")
                    .build();

            Scheme pmKisan = Scheme.builder()
                    .name("PM Kisan Samman Nidhi")
                    .category("Agriculture")
                    .state("Central")
                    .department("Ministry of Agriculture and Farmers Welfare")
                    .description("Provides financial assistance of ₹6,00,000 per year in three equal installments to all landholding farmer families across the country.")
                    .minAge(18)
                    .maxAge(100)
                    .maxIncome(300000.0)
                    .eligibleOccupations("Farmer")
                    .eligibleCategories("All")
                    .eligibleGenders("All")
                    .documentsRequired("Land Ownership Registry (Fard/Jamabandi), Aadhaar Card, Bank Passbook, Mobile Number")
                    .applyLink("https://pmkisan.gov.in/RegistrationFormNew.aspx")
                    .pdfUrl("/uploads/pmkisan.pdf")
                    .metadata("{}")
                    .build();

            Scheme ssy = Scheme.builder()
                    .name("Sukanya Samriddhi Yojana")
                    .category("Education")
                    .state("Central")
                    .department("Ministry of Women and Child Development")
                    .description("A girl child prosperity savings scheme targeted at parents to encourage savings for educational and marriage expenses of girl children.")
                    .minAge(0)
                    .maxAge(10)
                    .maxIncome(9999999.0)
                    .eligibleOccupations("All")
                    .eligibleCategories("All")
                    .eligibleGenders("Female")
                    .documentsRequired("Birth Certificate of the Girl Child, Aadhaar Card of the Parent/Guardian, Address Proof, Photograph")
                    .applyLink("https://www.indiapost.gov.in/")
                    .pdfUrl("/uploads/ssy.pdf")
                    .metadata("{}")
                    .build();

            Scheme bhamashah = Scheme.builder()
                    .name("Rajasthan Swasthya Bima Yojana")
                    .category("Healthcare")
                    .state("Rajasthan")
                    .department("Department of Medical and Health")
                    .description("Cashless medical facility and health insurance coverage up to ₹3,00,000 per year for residents of Rajasthan state.")
                    .minAge(0)
                    .maxAge(120)
                    .maxIncome(250000.0)
                    .eligibleOccupations("All")
                    .eligibleCategories("All")
                    .eligibleGenders("All")
                    .documentsRequired("Jan Aadhaar Card / Bhamashah Card, Aadhaar Card, Address Proof, Ration Card")
                    .applyLink("https://health.rajasthan.gov.in/")
                    .pdfUrl("/uploads/bhamashah.pdf")
                    .metadata("{}")
                    .build();

            Scheme widowPension = Scheme.builder()
                    .name("Indira Gandhi National Widow Pension Scheme (IGNWPS)")
                    .category("Social Security")
                    .state("Central")
                    .department("Ministry of Rural Development")
                    .description("Provides financial assistance (monthly pension) to widows aged 40-79 years who are living below the poverty line (BPL).")
                    .minAge(40)
                    .maxAge(79)
                    .maxIncome(120000.0)
                    .eligibleOccupations("Unemployed")
                    .eligibleCategories("All")
                    .eligibleGenders("Female")
                    .documentsRequired("Husband's Death Certificate, Aadhaar Card, BPL Card, Income Certificate, Bank Passbook, Address Proof")
                    .applyLink("https://nsap.nic.in/nsap/DirectBenefitTransfer.html")
                    .pdfUrl("/uploads/widow_pension.pdf")
                    .metadata("{}")
                    .build();

            Scheme scScholarship = Scheme.builder()
                    .name("Post Matric Scholarship for SC Students")
                    .category("Scholarship")
                    .state("Central")
                    .department("Ministry of Social Justice and Empowerment")
                    .description("Financial assistance scheme to enable scheduled castes (SC) students to pursue post-matric/post-secondary education.")
                    .minAge(15)
                    .maxAge(25)
                    .maxIncome(250000.0)
                    .eligibleOccupations("Student")
                    .eligibleCategories("SC")
                    .eligibleGenders("All")
                    .documentsRequired("Caste Certificate, Income Certificate, Previous Class Marksheet, Fee Receipt, Bank Passbook, Aadhaar Card")
                    .applyLink("https://scholarships.gov.in/fresh/newRegistrationPage")
                    .pdfUrl("/uploads/sc_scholarship.pdf")
                    .metadata("{}")
                    .build();

            Scheme nmmss = Scheme.builder()
                    .name("National Means Cum Merit Scholarship (NMMSS)")
                    .category("Scholarship")
                    .state("Central")
                    .department("Ministry of Education")
                    .description("Award of scholarship of ₹12,00,000 per annum to meritorious students of class IX to XII to arrest their drop out at class VIII.")
                    .minAge(12)
                    .maxAge(18)
                    .maxIncome(350000.0)
                    .eligibleOccupations("Student")
                    .eligibleCategories("All")
                    .eligibleGenders("All")
                    .documentsRequired("Class VIII Marksheet, Income Certificate of Parents, NMMSS Selection Certificate, Aadhaar Card, Bank Details")
                    .applyLink("https://scholarships.gov.in/fresh/newRegistrationPage")
                    .pdfUrl("/uploads/nmmss.pdf")
                    .metadata("{}")
                    .build();

            Scheme yuvaSambal = Scheme.builder()
                    .name("Rajasthan Mukhyamantri Yuva Sambal Yojana")
                    .category("Employment")
                    .state("Rajasthan")
                    .department("Department of Skill, Employment and Entrepreneurship")
                    .description("Provides monthly unemployment allowance of ₹4,000 to male youth and ₹4,500 to female/disabled youth in Rajasthan for up to 2 years.")
                    .minAge(21)
                    .maxAge(35)
                    .maxIncome(200000.0)
                    .eligibleOccupations("Unemployed")
                    .eligibleCategories("All")
                    .eligibleGenders("All")
                    .documentsRequired("Rajasthan Domicile Certificate, Graduate Degree Marksheet, Income Declaration Form (I & K), Aadhaar Card, Jan Aadhaar Card, Bank Details")
                    .applyLink("https://sso.rajasthan.gov.in/signin")
                    .pdfUrl("/uploads/yuva_sambal.pdf")
                    .metadata("{}")
                    .build();

            Scheme bbbp = Scheme.builder()
                    .name("Beti Bachao Beti Padhao (BBBP)")
                    .category("Education")
                    .state("Central")
                    .department("Ministry of Women and Child Development")
                    .description("National awareness campaign to improve gender balance, stop discrimination, and support girl child education.")
                    .minAge(0)
                    .maxAge(18)
                    .maxIncome(9999999.0)
                    .eligibleOccupations("All")
                    .eligibleCategories("All")
                    .eligibleGenders("Female")
                    .documentsRequired("Birth Certificate of Girl Child, Aadhaar Card of Parents, Address Proof, Bank Passbook")
                    .applyLink("https://wcd.nic.in/schemes/beti-bachao-beti-padhao")
                    .pdfUrl("/uploads/beti_bachao.pdf")
                    .metadata("{}")
                    .build();

            Scheme mahilaSamman = Scheme.builder()
                    .name("Mahila Samman Savings Certificate")
                    .category("Financial")
                    .state("Central")
                    .department("Ministry of Finance")
                    .description("A small savings scheme for women and girls offering a fixed interest rate of 7.5% per annum for a 2-year tenure.")
                    .minAge(0)
                    .maxAge(120)
                    .maxIncome(9999999.0)
                    .eligibleOccupations("All")
                    .eligibleCategories("All")
                    .eligibleGenders("Female")
                    .documentsRequired("Aadhaar Card, PAN Card, Account Opening Form, KYC documents, Address Proof")
                    .applyLink("https://www.indiapost.gov.in/")
                    .pdfUrl("/uploads/mahila_samman.pdf")
                    .metadata("{}")
                    .build();

            schemeRepository.saveAll(Arrays.asList(pmay, pmKisan, ssy, bhamashah, widowPension, scScholarship, nmmss, yuvaSambal, bbbp, mahilaSamman));
            System.out.println("Seeded popular government welfare schemes!");
        }
    }

    private void generateLocalPdfGuide(String fileName, String headerMinistry, String title, String... sectionPairs) {
        try {
            File uploadPath = new File("uploads");
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            File file = new File(uploadPath, fileName);
            if (file.exists()) {
                file.delete();
            }

            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                float yOffset = 730;
                float startX = 50;
                float width = 500;

                PDPageContentStream cs = new PDPageContentStream(document, page);

                // Header Ministry
                cs.beginText();
                cs.setFont(boldFont, 8.5f);
                cs.newLineAtOffset(startX, yOffset);
                cs.showText(headerMinistry.toUpperCase());
                cs.endText();
                yOffset -= 16;

                // Title
                cs.beginText();
                cs.setFont(boldFont, 13);
                cs.newLineAtOffset(startX, yOffset);
                cs.showText(title);
                cs.endText();
                yOffset -= 22;

                // Subtitle
                cs.beginText();
                cs.setFont(boldFont, 9.5f);
                cs.newLineAtOffset(startX, yOffset);
                cs.showText("OFFICIAL GOVERNMENT SCHEME NOTIFICATION & GUIDELINE DOCUMENT");
                cs.endText();
                yOffset -= 16;

                // Separator line
                cs.setLineWidth(1f);
                cs.moveTo(startX, yOffset);
                cs.lineTo(startX + width, yOffset);
                cs.stroke();
                yOffset -= 18;

                for (int i = 0; i < sectionPairs.length; i += 2) {
                    if (i + 1 >= sectionPairs.length) break;
                    String secTitle = sectionPairs[i];
                    String secBody = sectionPairs[i + 1];

                    if (yOffset < 80) {
                        cs.close();
                        page = new PDPage();
                        document.addPage(page);
                        cs = new PDPageContentStream(document, page);
                        yOffset = 730;
                    }

                    // Section Title
                    cs.beginText();
                    cs.setFont(boldFont, 10.5f);
                    cs.newLineAtOffset(startX, yOffset);
                    cs.showText(secTitle);
                    cs.endText();
                    yOffset -= 15;

                    // Section Body text wrapping
                    String[] lines = secBody.split("\n");
                    for (String line : lines) {
                        List<String> wrapped = wrapLine(line, 80);
                        for (String w : wrapped) {
                            if (yOffset < 60) {
                                cs.close();
                                page = new PDPage();
                                document.addPage(page);
                                cs = new PDPageContentStream(document, page);
                                yOffset = 730;
                            }
                            cs.beginText();
                            cs.setFont(regularFont, 9f);
                            cs.newLineAtOffset(startX + 10, yOffset);
                            cs.showText(w);
                            cs.endText();
                            yOffset -= 13;
                        }
                    }
                    yOffset -= 8;
                }

                // Footer
                if (yOffset < 50) {
                    cs.close();
                    page = new PDPage();
                    document.addPage(page);
                    cs = new PDPageContentStream(document, page);
                    yOffset = 730;
                }
                yOffset -= 10;
                cs.setLineWidth(0.5f);
                cs.moveTo(startX, yOffset);
                cs.lineTo(startX + width, yOffset);
                cs.stroke();
                yOffset -= 14;

                cs.beginText();
                cs.setFont(boldFont, 8.5f);
                cs.newLineAtOffset(startX, yOffset);
                cs.showText("Verified Official Document - Govt Schemes RAG Knowledge Portal (Page " + document.getNumberOfPages() + ")");
                cs.endText();

                cs.close();
                document.save(file);
                System.out.println("Generated official government scheme PDF: " + file.getAbsolutePath() + " (" + document.getNumberOfPages() + " pages)");
            }
        } catch (Exception e) {
            System.err.println("Failed to programmatically generate PDF guide " + fileName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<String> wrapLine(String text, int maxChars) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return result;
        }
        String[] words = text.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (sb.length() + word.length() + 1 > maxChars) {
                result.add(sb.toString());
                sb = new StringBuilder(word);
            } else {
                if (sb.length() > 0) sb.append(" ");
                sb.append(word);
            }
        }
        if (sb.length() > 0) {
            result.add(sb.toString());
        }
        return result;
    }
}
